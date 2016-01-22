package btspn.sr;

import javaslang.Function1;
import javaslang.Function2;
import javaslang.collection.List;
import org.testng.annotations.Test;

import java.util.Objects;

import static btspn.sr.CommandFunction.cp;
import static btspn.sr.EventFunction.ep;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

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
        return List.of(new FirstNameChanged(cmd.firstName));
    }


    public static List createPerson(CreatePerson cmd, Person personN) {
        return List.of(new PersonCreated(cmd.firstName, cmd.lastName));
    }


    @Test
    public void testEvent() throws Exception {


        Holder<Person> holder = Holder.hold(new Person("Jane", "Doe"));
        Function1<Object, Person> sr = Dispatch.<Person, State>state(
                Dispatch.<Person, State>event()
                        .put(ChangeFirstName.class,
                                ep(StateReducerTest::changeFirstName)))
                .apply(holder, new State());

        Person newPerson = sr.apply(new ChangeFirstName("John"));
        assertEquals(newPerson.firstName, "John");
        assertEquals(newPerson.lastName, "Doe");
        assertSame(newPerson, holder.get());
    }

    @Test
    public void testCmd() throws Exception {
        EventStore eventStore = InMemoryStore.empty();
        Holder<Person> holder = Holder.hold(new Person("Jane", "Doe"));

        Function2<Object, Object, Person> sr = Dispatch
                .<Person, State>esState(
                        Dispatch.<Person, State>cmd()
                                .put(CreatePerson.class, cp(StateReducerTest::createPerson))
                                .put(ChangeFirstName.class, cp(StateReducerTest::changeFirstNameEvent))
                        ,
                        Dispatch.<Person, State>event()
                                .put(PersonCreated.class, ep(StateReducerTest::personCreated))
                                .put(FirstNameChanged.class, ep(StateReducerTest::firstNameChanged)))
                .apply(eventStore, new State());

        sr.apply(1, new CreatePerson("Jane", "Doe"));
        Person newPerson = sr.apply(1, new ChangeFirstName("John"));
        assertEquals(newPerson.firstName, "John");
        assertEquals(newPerson.lastName, "Doe");
        assertEquals(List.of(new PersonCreated("Jane", "Doe"), new FirstNameChanged("John")), eventStore.events(1));
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