/**
 * 
 */
package fr.n7.stl.minic.ast.expression;

import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.minic.ast.type.AtomicType;
import fr.n7.stl.minic.ast.type.Type;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.TAMFactory;

/**
 * Abstract Syntax Tree node for a conditional expression.
 * @author Marc Pantel
 *
 */
public class AbstractConditional<ExpressionKind extends Expression> implements Expression {

	/**
	 * AST node for the expression whose value is the condition for the conditional expression.
	 */
	protected Expression condition;
	
	/**
	 * AST node for the expression whose value is the then parameter for the conditional expression.
	 */
	protected ExpressionKind thenExpression;
	
	/**
	 * AST node for the expression whose value is the else parameter for the conditional expression.
	 */
	protected ExpressionKind elseExpression;
	
	/**
	 * Builds a binary expression Abstract Syntax Tree node from the left and right sub-expressions
	 * and the binary operation.
	 * @param _left : Expression for the left parameter.
	 * @param _operator : Binary Operator.
	 * @param _right : Expression for the right parameter.
	 */
	public AbstractConditional(Expression _condition, ExpressionKind _then, ExpressionKind _else) {
		this.condition = _condition;
		this.thenExpression = _then;
		this.elseExpression = _else;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#collect(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean collectAndPartialResolve(HierarchicalScope<Declaration> _scope) {
		if (elseExpression == null) {
			return this.condition.collectAndPartialResolve(_scope) && this.thenExpression.collectAndPartialResolve(_scope);
		} else {
			return this.condition.collectAndPartialResolve(_scope) && this.thenExpression.collectAndPartialResolve(_scope) && this.elseExpression.collectAndPartialResolve(_scope);
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.expression.Expression#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		if (elseExpression == null) {
			return this.condition.completeResolve(_scope) && this.thenExpression.completeResolve(_scope);
		} else {
			return this.condition.completeResolve(_scope) && this.thenExpression.completeResolve(_scope) && this.elseExpression.completeResolve(_scope);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.condition + " ? " + this.thenExpression + " : " + this.elseExpression + ")";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getType()
	 */
	@Override
	public Type getType() {
		if (elseExpression == null) {
			if(this.condition.getType().equalsTo(AtomicType.BooleanType)){
				return  this.thenExpression.getType();
			} else{
				throw new RuntimeException("Condition should be a Boolean value");
			}
		} else {
			if(this.condition.getType().equalsTo(AtomicType.BooleanType)){
				if(this.thenExpression.getType().compatibleWith(thenExpression.getType())){
					return  this.thenExpression.getType();
				} else {
					throw new RuntimeException("expression should have a compatible Type in abstractConditional");
				}
			} else{
				throw new RuntimeException("Condition should be a Boolean value");
			}
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();
		int LabelNumber = _factory.createLabelNumber();
		if (elseExpression == null) {
			fragment.append(condition.getCode(_factory));
			fragment.add(_factory.createJumpIf("FIN_IF"+LabelNumber, 0));
			fragment.append(thenExpression.getCode(_factory));
			fragment.addSuffix("FIN_IF"+LabelNumber);
			
		} else {
			fragment.append(condition.getCode(_factory));
			fragment.add(_factory.createJumpIf("ELSE"+LabelNumber, 0));
			fragment.append(thenExpression.getCode(_factory));
			fragment.add(_factory.createJump("END_IF_ELSE"+LabelNumber));
			fragment.addSuffix("ELSE"+LabelNumber);
			fragment.append(elseExpression.getCode(_factory));
			fragment.addSuffix("END_IF_ELSE"+LabelNumber);
		}
		return fragment;
	}

}
