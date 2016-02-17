package com.drencak.sr;

import javaslang.Tuple4;
import javaslang.control.Match;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Objects;

public class PredicateTest {
    public static class MyState {
        public final boolean state;
        public final String value;

        public MyState(boolean state, String value) {
            this.state = state;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MyState myState = (MyState) o;
            return state == myState.state &&
                    Objects.equals(value, myState.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, value);
        }

        @Override
        public String toString() {
            return "MyState{" +
                    "state=" + state +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class MyCtx {

    }

    public static class SetValue {
        public final String value;

        public SetValue(String value) {
            this.value = value;
        }
    }

    public static MyState processActive(SetValue cmd, MyState state) {
        return new MyState(state.state, "active " + cmd.value);
    }

    public static MyState processPassive(SetValue cmd, MyState state) {
        return new MyState(state.state, "passive " + cmd.value);
    }

    public static MyState create(MyState state) {
        return new MyState(false, "");
    }

    public static MyState toggle(MyState state) {
        return new MyState(!state.state, state.value);
    }

    public static class ToggleState {

    }

    @Test
    public void testByPredicate() throws Exception {

        EventFunction<MyState, MyCtx> sr = Dispatch.eventByPredicate(Match
                .when((Tuple4<Object, MyState, MyState, MyCtx> t) -> t._2 != null && t._2.state && t._1 instanceof SetValue)
                .then(EventFunction.p(PredicateTest::processActive, MyCtx.class))

                .when((Tuple4<Object, MyState, MyState, MyCtx> t) -> t._2 != null && !t._2.state && t._1 instanceof SetValue)
                .then(EventFunction.p(PredicateTest::processPassive))

                .otherwise(Dispatch.<MyState, MyCtx>event()
                        .on(ToggleState.class, EventFunction.i(PredicateTest::toggle))
                        .orElse(EventFunction.i(PredicateTest::create)))
        );


        MyCtx ctx = new MyCtx();
        MyState state = sr.reduce(null, ctx, new Object());
        Assert.assertEquals(state, new MyState(false, ""));

        state = sr.reduce(state, ctx, new SetValue("value"));
        Assert.assertEquals(state, new MyState(false, "passive value"));


        state = sr.reduce(state, ctx, new ToggleState());
        Assert.assertEquals(state, new MyState(true, "passive value"));

        state = sr.reduce(state, ctx, new SetValue("valueX"));
        Assert.assertEquals(state, new MyState(true, "active valueX"));

    }

    public static boolean isActive(MyState state) {
        return state != null && state.state;
    }

    public static boolean isPassive(MyState state) {
        return state != null && !state.state;
    }


    @Test
    public void testByState() throws Exception {

        EventFunction<MyState, MyCtx> sr = Dispatch.eventByState(Match
                .when(PredicateTest::isPassive)
                .then(Dispatch.<MyState, MyCtx>event()
                        .on(ToggleState.class, EventFunction.i(PredicateTest::toggle))
                        .on(SetValue.class, EventFunction.p(PredicateTest::processPassive, MyCtx.class))
                        .cast())

                .when(PredicateTest::isActive)
                .then(Dispatch.<MyState, MyCtx>event()
                        .on(ToggleState.class, EventFunction.i(PredicateTest::toggle))
                        .on(SetValue.class, EventFunction.p(PredicateTest::processActive, MyCtx.class))
                        .cast())
                .otherwise(EventFunction.i(PredicateTest::create))
        );


        MyCtx ctx = new MyCtx();
        MyState state = sr.reduce(null, ctx, new Object());
        Assert.assertEquals(state, new MyState(false, ""));

        state = sr.reduce(state, ctx, new SetValue("value"));
        Assert.assertEquals(state, new MyState(false, "passive value"));


        state = sr.reduce(state, ctx, new ToggleState());
        Assert.assertEquals(state, new MyState(true, "passive value"));

        state = sr.reduce(state, ctx, new SetValue("valueX"));
        Assert.assertEquals(state, new MyState(true, "active valueX"));

    }

}
