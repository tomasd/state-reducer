# State reducer

Interfaces for CQRS, command and event processing. Business logic can be created by functional composition. 

# Using state reducer

Add dependency to your pom.xml:

```xml
<dependency>
    <groupId>com.drencak.state-reducer</groupId>
    <artifactId>state-reducer</artifactId>
    <version>0.2</version>
</dependency>
```

# Example usage

## CQRS

In CQRS there are commands and events. Each command can generate multiple events. Events are stored inside event store. To process command, function must at first load all events from the event store and replay them to obtain
 current state of the entity.
 
```java

    public static Person changeFirstName(ChangeFirstName cmd, Person personN) {
        return new Person(cmd.firstName, personN.lastName);
    }

    public static Person changeFirstNameE(FirstNameChanged cmd, Person personN) {
        return new Person(cmd.firstName, personN.lastName);
    }


    public static List changeFirstNameEvent(ChangeFirstName cmd, Person personN) {
        return List.of(new FirstNameChanged(cmd.firstName));
    }


    public static List createPerson(CreatePerson cmd, Person personN) {
        return List.of(new PersonCreated(cmd.firstName, cmd.lastName));
    }
```

```java
EventStore eventStore = InMemoryStore.empty();

EsStateReducer<Person, State> sr = EsStateReducer.of(
    Dispatch.<Person, State>cmd()
            .on(CreatePerson.class, CommandFunction.pc(StateReducerTest::createPerson))
            .on(ChangeFirstName.class, CommandFunction.pc(StateReducerTest::changeFirstNameEvent)),
    Dispatch.<Person, State>event()
            .on(PersonCreated.class, EventFunction.p(StateReducerTest::personCreated))
            .on(FirstNameChanged.class, EventFunction.p(StateReducerTest::firstNameChanged)),
    1);
    
int personId = 1;
sr.apply(eventStore, State::new, personId, new CreatePerson("Jane", "Doe"));
Person newPerson = sr.apply(eventStore, State::new, personId, new ChangeFirstName("John"));

assertEquals(newPerson.firstName, "John");
assertEquals(newPerson.lastName, "Doe");
assertEquals(List.of(new PersonCreated("Jane", "Doe"), new FirstNameChanged("John")), eventStore.events(1));
```

In this example 2 commands (CreatePerson and ChangeFirstName) were sent into the processor. These created 
2 corresponding events (PersonCreated and FirstNameChanged). Events were stored in event Store.

## Command processing
Command processing is subset of CQRS where input of the system is based solely on events. Events are applied to the state and state is persisted in holder (kind of Repository pattern).

```java
Holder<Person> holder = Holder.hold(new Person("Jane", "Doe"));

StateReducer<Person, State> sr = StateReducer.of(
                Dispatch.<Person, State>event()
                        .on(ChangeFirstName.class,
                                EventFunction.p(StateReducerTest::changeFirstName)));
                                
Person newPerson = sr.apply(holder, State::new, new ChangeFirstName("John"));
assertEquals(newPerson.firstName, "John");
assertEquals(newPerson.lastName, "Doe");
assertSame(newPerson, holder.get());
```

# Concepts

## Command function

One command can be processed in multiple steps, each generating 0..n events. Each step should introspect initial and current state.

Header:
f(command, initialState, currentState, ctx, player) -> tuple(events, newState)

As this function is very complex we can use several command adapters which can be used in different situations. Wrapper internally calls provided function and replays generated events on current state. It uses player for replaying events. Player is event function described lower.

### Process command function

f(command, currentState) -> events

Processes command in context of actual state. Useful in:

* command validation
* command process

### Invariant command function

f(currentState) -> events

Processes actual state. Useful in:

* generating derived values
* validation of state invariants

### Thread command function

f(commandFunction1, commandFunction2, ...commandFunctionN)

Represent multiple command functions as 1.

### Dispatcher command function

f(
   predicate1, commandFunction1,
   predicate2, commandFunction2
   .
   .
   predicateN, commandFunctionN
   )

Business logic should be represented as 1 function. As there are multiple commands, they need to be processes separately.

<hr/>

## Event function

Generated events should be processed. Event function changes current state. Event can be processed in multiple steps, each advancing state into new one.

Header:
f(event, initialState, currentState, ctx) -> new state

This function is also complex, so we can use adapters.

### Process event function

Process event in context of current state.

f(event, state) -> new state

### Invariant event function

Validate or recalculate derived values from current state.

f(state) -> new state

### Thread event function

Represent multiple event functions as 1.

f(eventFunction1, eventFunction2,...eventFunctionN) -> new state

### Dispatcher event function

Dispatch events to multiple functions.

f(
   predicate1, eventFunction1,
   predicate2, eventFunction2
   .
   .
   predicateN, eventFunctionN
   )

<hr/>

## State reducer

State reducer is side effecting function applying command, generating and storing events into event store.

Header:
f(eventStore, ctx, id, command) -> new state
