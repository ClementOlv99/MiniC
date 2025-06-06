/**
 * 
 */
package fr.n7.stl.minic.ast.expression.accessible;

/**
 * @author Marc Pantel
 *
 */
public enum UnaryOperator {
	
	/**
	 * Boolean negation
	 */
	Negate,
	/**
	 * Numeric opposite
	 */
	Opposite;

	@Override
	public String toString() {
		switch (this) {
		case Negate: return "!";
		case Opposite: return "-";
		default: throw new IllegalArgumentException( "The default case should never be triggered.");		
		}
	}

}
