package btspn.sr;

import btspn.sr.cmd.EventStore;
import btspn.sr.cmd.InMemoryStore;
import btspn.sr.event.Holder;
import javaslang.Tuple;
import javaslang.collection.List;
import javaslang.control.Match;
import org.testng.annotations.Test;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static btspn.sr.CommandFunction.pc;
import static btspn.sr.EventFunction.p;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

public class StateReducerTest {
    public static class State {

    }

    public static class Person {
        public final String firstName;
        public final String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return Objects.equals(firstName, person.firstName) &&
                    Objects.equals(lastName, person.lastName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName);
        }
    }

    public static class ChangeFirstName {
        public final String firstName;

        public ChangeFirstName(String firstName) {
            this.firstName = firstName;
        }
    }

    public static class FirstNameChanged {
        public final String firstName;

        public FirstNameChanged(String firstName) {
            this.firstName = firstName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FirstNameChanged that = (FirstNameChanged) o;
            return Objects.equals(firstName, that.firstName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName);
        }
    }

    public static Person changeFirstName(ChangeFirstName cmd, Person personN) {
        return new Person(cmd.firstName, personN.lastName);
    }

    public static Person changeFirstNameE(FirstNameChanged cmd, Person personN) {
        return new Person(cmd.firstName, personN.lastName);
    }


    public static List changeFirstNameEvent(ChangeFirstName cmd, Person personN) {
        List events = List.empty();
        if (personN == null) {
            events = events.append(new PersonCreated(cmd.firstName, null));
        }
        return events.append(new FirstNameChanged(cmd.firstName));
    }


    public static List createPerson(CreatePerson cmd, Person personN) {
        return List.of(new PersonCreated(cmd.firstName, cmd.lastName));
    }


    @Test
    public void testEvent() throws Exception {
        testEvent(StateReducer.of(
                Dispatch.<Person, State>event()
                        .on(ChangeFirstName.class,
                                p(StateReducerTest::changeFirstName))));
        testEvent(StateReducer.of(
                Dispatch.event(Match
                        .whenType(ChangeFirstName.class)
                        .then(p(StateReducerTest::changeFirstName)))));
    }

    private void testEvent(StateReducer<Person, State> sr) {
        Holder<Person> holder = Holder.hold(new Person("Jane", "Doe"));

        Person newPerson = sr.apply(holder, State::new, new ChangeFirstName("John"));
        assertEquals(newPerson.firstName, "John");
        assertEquals(newPerson.lastName, "Doe");
        assertSame(newPerson, holder.get());
    }

    @Test
    public void testCmd() throws Exception {
        testCmd(EsStateReducer.of(
                Dispatch.<Person, State>cmd()
                        .on(CreatePerson.class, pc(StateReducerTest::createPerson))
                        .on(ChangeFirstName.class, pc(StateReducerTest::changeFirstNameEvent)),
                Dispatch.<Person, State>event()
                        .on(PersonCreated.class, p(StateReducerTest::personCreated))
                        .on(FirstNameChanged.class, p(StateReducerTest::firstNameChanged)),
                1));
        testCmd(EsStateReducer.of(
                Dispatch.cmd(Match
                        .whenType(CreatePerson.class)
                        .then(pc(StateReducerTest::createPerson, State.class))

                        .whenType(ChangeFirstName.class)
                        .then(pc(StateReducerTest::changeFirstNameEvent))),
                Dispatch.event(Match
                        .whenType(PersonCreated.class)
                        .then(p(StateReducerTest::personCreated, State.class))

                        .whenType(FirstNameChanged.class)
                        .then(p(StateReducerTest::firstNameChanged))),
                1));
    }

    private void testCmd(EsStateReducer<Person, State> sr) {
        EventStore eventStore = InMemoryStore.empty();


        sr.apply(eventStore, State::new, 1, new CreatePerson("Jane", "Doe"));
        Person newPerson = sr.apply(eventStore, State::new, 1, new ChangeFirstName("John"));
        assertEquals(newPerson.firstName, "John");
        assertEquals(newPerson.lastName, "Doe");
        assertEquals(List.of(new PersonCreated("Jane", "Doe"), new FirstNameChanged("John")), eventStore.events(1));
        assertEquals(eventStore.lastSnapshot(1), Tuple.of(2, new Person("John", "Doe")));
    }

    private static <C> Predicate<C> assignableFrom(Class clazz) {
        return o -> clazz.isAssignableFrom(o.getClass());
    }

    @Test
    public void testCmd2() throws Exception {
        EventStore eventStore = InMemoryStore.empty();


        EsStateReducer<Person, State> sr = EsStateReducer.of(
                Dispatch.<Person, State>cmd()
                        .on(CreatePerson.class, pc(StateReducerTest::createPerson))
                        .on(ChangeFirstName.class, pc(StateReducerTest::changeFirstNameEvent)),
                Dispatch.<Person, State>event()
                        .on(PersonCreated.class, p(StateReducerTest::personCreated))
                        .on(FirstNameChanged.class, p(StateReducerTest::firstNameChanged)),
                1);

        Person newPerson = sr.apply(eventStore, State::new, 1, new ChangeFirstName("John"));
        assertEquals(newPerson.firstName, "John");
        assertEquals(newPerson.lastName, null);
        assertEquals(List.of(new PersonCreated("John", null), new FirstNameChanged("John")), eventStore.events(1));
        assertEquals(eventStore.lastSnapshot(1), Tuple.of(2, new Person("John", null)));
    }

    @Test
    public void testSideEffectEvent() throws Exception {
        new StateReducer<Person, State>() {

            @Override
            public Person apply(Holder<Person> personHolder, Supplier<State> stateSupplier, Object o) {
                return null;
            }
        };
    }

    private static Person firstNameChanged(FirstNameChanged event, Person personN) {
        return new Person(event.firstName, personN.lastName);
    }

    private static Person personCreated(PersonCreated event, Person personN) {
        return new Person(event.firstName, event.lastName);
    }

    private static class PersonCreated {
        private final String firstName;
        private final String lastName;

        public PersonCreated(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PersonCreated that = (PersonCreated) o;
            return Objects.equals(firstName, that.firstName) &&
                    Objects.equals(lastName, that.lastName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(firstName, lastName);
        }
    }

    private class CreatePerson {
        public final String firstName;
        public final String lastName;

        public CreatePerson(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
}