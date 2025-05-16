package fr.n7.stl.minic.ast.expression;

import fr.n7.stl.minic.ast.instruction.declaration.TypeDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.NamedType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.minic.ast.type.declaration.FieldDeclaration;
import fr.n7.stl.util.Logger;

/**
 * Common elements between left (Assignable) and right (Expression) end sides of assignments. These elements
 * share attributes, toString and getType methods.
 * @author Marc Pantel
 *
 */
public abstract class AbstractField<RecordKind extends Expression> implements Expression {

	protected RecordKind record;
	protected String name;
	protected FieldDeclaration field;
	
	/**
	 * Construction for the implementation of a record field access expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field access expression.
	 * @param _name Name of the field in the record field access expression.
	 */
	public AbstractField(RecordKind _record, String _name) {
		this.record = _record;
		this.name = _name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.record + "." + this.name;
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		if (((HierarchicalScope<Declaration>)_scope).knows(this.name)) {
			Declaration _declaration = _scope.get(this.name);
			if (_declaration instanceof FieldDeclaration fld) {
				this.field = fld;
				return true;
			} else {
				Logger.error("The declaration for " + this.name + " is of the wrong kind.");
				return false;
			}
		} else {
			Logger.error("The identifier " + this.name + " has not been found.");
			return false;	
		}

	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.HierarchicalScope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		return record.completeResolve(_scope);
	}

	/**
	 * Synthesized Semantics attribute to compute the type of an expression.
	 * @return Synthesized Type of the expression.
	 */
	public Type getType() {
		return field.getType();
	}

}