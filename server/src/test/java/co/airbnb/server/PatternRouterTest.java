package co.airbnb.server;

import java.util.ArrayList;
import java.util.List;

class PatternRouterTest {

    private final PatternRouter router = new PatternRouter();

    static final class TestResult {
        private final String name;
        private final boolean passed;
        private final String detail;

        TestResult(String name, boolean passed, String detail) {
            this.name = name;
            this.passed = passed;
            this.detail = detail;
        }
    }

    public static void main(String[] args) {
        PatternRouterTest suite = new PatternRouterTest();
        List<TestResult> results = new ArrayList<>();
        results.add(suite.testSingleton());
        results.add(suite.testFactoryMethod());
        results.add(suite.testAbstractFactory());
        results.add(suite.testBuilder());
        results.add(suite.testPrototype());
        results.add(suite.testAdapter());
        results.add(suite.testBridge());
        results.add(suite.testComposite());
        results.add(suite.testDecorator());
        results.add(suite.testFacade());
        results.add(suite.testFlyweight());
        results.add(suite.testProxy());
        results.add(suite.testChainOfResponsibility());
        results.add(suite.testCommand());
        results.add(suite.testInterpreter());
        results.add(suite.testIterator());
        results.add(suite.testMediator());
        results.add(suite.testMemento());
        results.add(suite.testObserver());
        results.add(suite.testState());
        results.add(suite.testStrategy());
        results.add(suite.testTemplateMethod());
        results.add(suite.testVisitor());

        int passed = 0;
        for (TestResult result : results) {
            String status = result.passed ? "PASS" : "FAIL";
            System.out.println(status + " - " + result.name + " - " + result.detail);
            if (result.passed) {
                passed++;
            }
        }

        System.out.println("Resumen: " + passed + "/" + results.size() + " pruebas aprobadas");
        if (passed != results.size()) {
            System.exit(1);
        }
    }

    TestResult testSingleton() {
        return expectContains("Singleton por código", "01", "Singleton");
    }

    TestResult testFactoryMethod() {
        return expectContains("Factory Method por código", "02", "Email enviado");
    }

    TestResult testAbstractFactory() {
        return expectContains("Abstract Factory por código", "03", "Botón web");
    }

    TestResult testBuilder() {
        return expectContains("Builder por código", "04", "Apartamento en Barcelona");
    }

    TestResult testPrototype() {
        return expectContains("Prototype por código", "05", "Apartamento base");
    }

    TestResult testAdapter() {
        return expectContains("Adapter por código", "06", "Stripe cargó");
    }

    TestResult testBridge() {
        return expectContains("Bridge por código", "07", "Precio:");
    }

    TestResult testComposite() {
        return expectContains("Composite por código", "08", "Edificio Principal");
    }

    TestResult testDecorator() {
        return expectContains("Decorator por código", "09", "+ Wi-Fi");
    }

    TestResult testFacade() {
        return expectContains("Facade por código", "10", "Reserva creada");
    }

    TestResult testFlyweight() {
        return expectContains("Flyweight por código", "11", "cache=2");
    }

    TestResult testProxy() {
        return expectContains("Proxy por código", "12", "Acceso restringido");
    }

    TestResult testChainOfResponsibility() {
        return expectContains("Chain of Responsibility por código", "13", "SpamFilter");
    }

    TestResult testCommand() {
        return expectContains("Command por código", "14", "Reserva creada");
    }

    TestResult testInterpreter() {
        return expectContains("Interpreter por código", "15", "InterpreterHandler");
    }

    TestResult testIterator() {
        return expectContains("Iterator por código", "16", "L1 L2 L3");
    }

    TestResult testMediator() {
        return expectContains("Mediator por código", "17", "Bienvenido");
    }

    TestResult testMemento() {
        return expectContains("Memento por código", "18", "2026-06-01");
    }

    TestResult testObserver() {
        return expectContains("Observer por código", "19", "notificado");
    }

    TestResult testState() {
        return expectContains("State por código", "20", "Pago permitido");
    }

    TestResult testStrategy() {
        return expectContains("Strategy por código", "21", "360.0");
    }

    TestResult testTemplateMethod() {
        return expectContains("Template Method por código", "22", "Verificación estándar");
    }

    TestResult testVisitor() {
        return expectContains("Visitor por código", "23", "108.0");
    }

    private TestResult expectContains(String name, String code, String expectedFragment) {
        String result = router.run(code);
        boolean passed = result != null && !result.isBlank() && result.contains(expectedFragment);
        String detail = passed ? result : "Resultado inesperado: " + result;
        return new TestResult(name, passed, detail);
    }
}
