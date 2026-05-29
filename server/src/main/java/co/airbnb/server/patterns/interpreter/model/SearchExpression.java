package co.airbnb.server.patterns.interpreter.model;

import java.util.List;

public abstract class SearchExpression {

    public abstract List<String> interpret(List<String> listings);
}