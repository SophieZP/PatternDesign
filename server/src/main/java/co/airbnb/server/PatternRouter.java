package co.airbnb.server;

import co.airbnb.server.patterns.abstractfactory.AbstractFactoryHandler;
import co.airbnb.server.patterns.adapter.AdapterHandler;
import co.airbnb.server.patterns.bridge.BridgeHandler;
import co.airbnb.server.patterns.builder.BuilderHandler;
import co.airbnb.server.patterns.chainofresponsibility.ChainHandler;
import co.airbnb.server.patterns.command.CommandHandler;
import co.airbnb.server.patterns.composite.CompositeHandler;
import co.airbnb.server.patterns.decorator.DecoratorHandler;
import co.airbnb.server.patterns.facade.FacadeHandler;
import co.airbnb.server.patterns.factorymethod.FactoryMethodHandler;
import co.airbnb.server.patterns.flyweight.FlyweightHandler;
import co.airbnb.server.patterns.interpreter.InterpreterHandler;
import co.airbnb.server.patterns.iterator.IteratorHandler;
import co.airbnb.server.patterns.mediator.MediatorHandler;
import co.airbnb.server.patterns.memento.MementoHandler;
import co.airbnb.server.patterns.observer.ObserverHandler;
import co.airbnb.server.patterns.prototype.PrototypeHandler;
import co.airbnb.server.patterns.proxy.ProxyHandler;
import co.airbnb.server.patterns.singleton.SingletonHandler;
import co.airbnb.server.patterns.state.StateHandler;
import co.airbnb.server.patterns.strategy.StrategyHandler;
import co.airbnb.server.patterns.templatemethod.TemplateMethodHandler;
import co.airbnb.server.patterns.visitor.VisitorHandler;

public class PatternRouter {

	public String run(String patternName) {
		String key = patternName == null ? "" : patternName.trim().toLowerCase();

		if ("singleton".equals(key) || "01".equals(key)) {
			return new SingletonHandler().demo();
		}
		if ("factorymethod".equals(key) || "02".equals(key)) {
			return new FactoryMethodHandler().demo();
		}
		if ("abstractfactory".equals(key) || "03".equals(key)) {
			return new AbstractFactoryHandler().demo();
		}
		if ("builder".equals(key) || "04".equals(key)) {
			return new BuilderHandler().demo();
		}
		if ("prototype".equals(key) || "05".equals(key)) {
			return new PrototypeHandler().demo();
		}
		if ("adapter".equals(key) || "06".equals(key)) {
			return new AdapterHandler().demo();
		}
		if ("bridge".equals(key) || "07".equals(key)) {
			return new BridgeHandler().demo();
		}
		if ("composite".equals(key) || "08".equals(key)) {
			return new CompositeHandler().demo();
		}
		if ("decorator".equals(key) || "09".equals(key)) {
			return new DecoratorHandler().demo();
		}
		if ("facade".equals(key) || "10".equals(key)) {
			return new FacadeHandler().demo();
		}
		if ("flyweight".equals(key) || "11".equals(key)) {
			return new FlyweightHandler().demo();
		}
		if ("proxy".equals(key) || "12".equals(key)) {
			return new ProxyHandler().demo();
		}
		if ("chainofresponsibility".equals(key) || "13".equals(key)) {
			return new ChainHandler().demo();
		}
		if ("command".equals(key) || "14".equals(key)) {
			return new CommandHandler().demo();
		}
		if ("interpreter".equals(key) || "15".equals(key)) {
			return new InterpreterHandler().demo();
		}
		if ("iterator".equals(key) || "16".equals(key)) {
			return new IteratorHandler().demo();
		}
		if ("mediator".equals(key) || "17".equals(key)) {
			return new MediatorHandler().demo();
		}
		if ("memento".equals(key) || "18".equals(key)) {
			return new MementoHandler().demo();
		}
		if ("observer".equals(key) || "19".equals(key)) {
			return new ObserverHandler().demo();
		}
		if ("state".equals(key) || "20".equals(key)) {
			return new StateHandler().demo();
		}
		if ("strategy".equals(key) || "21".equals(key)) {
			return new StrategyHandler().demo();
		}
		if ("templatemethod".equals(key) || "22".equals(key)) {
			return new TemplateMethodHandler().demo();
		}
		if ("visitor".equals(key) || "23".equals(key)) {
			return new VisitorHandler().demo();
		}

		return "Patrón no encontrado: " + patternName;
	}
}
