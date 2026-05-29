package co.airbnb.server.patterns.interpreter.model;

import java.util.List;

public class AndExpression extends SearchExpression {

    private final SearchExpression left;
    private final SearchExpression right;

    public AndExpression(SearchExpression left, SearchExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public List<String> interpret(List<String> listings) {
        return right.interpret(left.interpret(listings));
    }
}