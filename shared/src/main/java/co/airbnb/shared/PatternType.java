package co.airbnb.shared;

public enum PatternType {

	SINGLETON("01", "Singleton", "Única instancia de configuración global"),
	FACTORY_METHOD("02", "Factory Method", "Creación encapsulada de notificaciones"),
	ABSTRACT_FACTORY("03", "Abstract Factory", "Familias de componentes UI"),
	BUILDER("04", "Builder", "Construcción paso a paso de listings"),
	PROTOTYPE("05", "Prototype", "Clonado de listings base"),
	ADAPTER("06", "Adapter", "Adaptación de pasarelas de pago"),
	BRIDGE("07", "Bridge", "Separación entre filtros e implementaciones"),
	COMPOSITE("08", "Composite", "Jerarquía de propiedades"),
	DECORATOR("09", "Decorator", "Amenidades agregadas dinámicamente"),
	FACADE("10", "Facade", "Acceso unificado a subsistemas"),
	FLYWEIGHT("11", "Flyweight", "Reuso de iconos de amenidades"),
	PROXY("12", "Proxy", "Control de acceso a perfiles"),
	CHAIN_OF_RESPONSIBILITY("13", "Chain of Responsibility", "Cadena de validación de reseñas"),
	COMMAND("14", "Command", "Encapsulación de reservas como objetos"),
	INTERPRETER("15", "Interpreter", "Interpretación de búsquedas"),
	ITERATOR("16", "Iterator", "Recorrido de colecciones"),
	MEDIATOR("17", "Mediator", "Comunicación centralizada en chat"),
	MEMENTO("18", "Memento", "Historial de estados de reservas"),
	OBSERVER("19", "Observer", "Notificación de cambios de precio"),
	STATE("20", "State", "Comportamiento según estado de reserva"),
	STRATEGY("21", "Strategy", "Cálculo de precios intercambiable"),
	TEMPLATE_METHOD("22", "Template Method", "Esqueleto del check-in"),
	VISITOR("23", "Visitor", "Operaciones sobre tipos de propiedades");

	private final String code;
	private final String displayName;
	private final String description;

	PatternType(String code, String displayName, String description) {
		this.code = code;
		this.displayName = displayName;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDescription() {
		return description;
	}

	public static PatternType fromValue(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim().toLowerCase();
		for (PatternType patternType : values()) {
			if (patternType.code.equals(normalized)
					|| patternType.name().toLowerCase().equals(normalized)
					|| patternType.displayName.toLowerCase().equals(normalized)) {
				return patternType;
			}
		}
		return null;
	}
}
