/**
 * 
 */
package fr.n7.stl.minic.ast.expression.accessible;
import fr.n7.stl.minic.ast.expression.AbstractField;
import fr.n7.stl.minic.ast.instruction.declaration.ParameterDeclaration;
import fr.n7.stl.minic.ast.instruction.declaration.VariableDeclaration;
import fr.n7.stl.tam.ast.Fragment;
import fr.n7.stl.tam.ast.Library;
import fr.n7.stl.tam.ast.Register;
import fr.n7.stl.tam.ast.TAMFactory;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for accessing a field in a record.
 * @author Marc Pantel
 *
 */
public class FieldAccess extends AbstractField<AccessibleExpression> implements AccessibleExpression {

	/**
	 * Construction for the implementation of a record field access expression Abstract Syntax Tree node.
	 * @param _record Abstract Syntax Tree for the record part in a record field access expression.
	 * @param _name Name of the field in the record field access expression.
	 */
	public FieldAccess(AccessibleExpression _record, String _name) {
		super(_record, _name);
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Expression#getCode(fr.n7.stl.tam.ast.TAMFactory)
	 */
	@Override
	public Fragment getCode(TAMFactory _factory) {
		Fragment fragment = _factory.createFragment();
		// Cas de field profondeur 0
		if(nwrecord instanceof IdentifierAccess irecord){

			//cas de variable
			if(irecord.expression instanceof VariableAccess varecord){
				VariableDeclaration rdecl = varecord.getDeclaration();

				fragment.add(_factory.createLoadA(rdecl.getRegister(),
												  rdecl.getOffset()));
				fragment.add(_factory.createLoadL(this.getField().getOffset()));
				fragment.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
				fragment.add(_factory.createLoadI(this.getField().getType().length()));

			} 
			//cas de paramétre de fonction (un struct en argument de fonction) 
			else if(irecord.expression instanceof ParameterAccess varecord){
				ParameterDeclaration rdecl = varecord.getDeclaration(); 

				fragment.add(_factory.createLoadA(Register.LB,
												  rdecl.getOffset()));
				fragment.add(_factory.createLoadL(this.getField().getOffset()));
				fragment.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
				fragment.add(_factory.createLoadI(this.getField().getType().length()));

			} 
			//cas de constante
			else {
				Logger.error("pourquoi une constante dans un struct?");
			}
		} 
		//cas de field imbriqué
		else if(nwrecord instanceof FieldAccess frec){
			fragment.append(rec(_factory,frec));
			fragment.add(_factory.createLoadL(this.getField().getOffset()));
			fragment.add(Library.IAdd);
			fragment.add(_factory.createLoadI(this.getField().getType().length()));
		} 
		//cas du flop
		else{
			Logger.error("L'acces des structs ont flop Oh no");
		}
		return fragment;
	}

	private static Fragment rec(TAMFactory _factory,FieldAccess fa) {
		Fragment fragment = _factory.createFragment();
		AccessibleExpression nwrecord = fa.nwrecord;
		// Cas terminale
		if(nwrecord instanceof IdentifierAccess inwrecord){
			//cas de variable
			if(inwrecord.expression instanceof VariableAccess vanwrecord){
				VariableDeclaration rdecl = vanwrecord.getDeclaration();

				fragment.add(_factory.createLoadA(rdecl.getRegister(),
												  rdecl.getOffset()));
				fragment.add(_factory.createLoadL(fa.getField().getOffset()));
				fragment.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));
			} 
			//cas de paramétre de fonction (un struct en argument de fonction) 
			else if(inwrecord.expression instanceof ParameterAccess panwrecord){				
				ParameterDeclaration rdecl = panwrecord.getDeclaration(); 

				fragment.add(_factory.createLoadA(Register.LB,
												  rdecl.getOffset()));
				fragment.add(_factory.createLoadL(fa.getField().getOffset()));
				fragment.add(TAMFactory.createBinaryOperator(BinaryOperator.Add));

			}
			//cas de constante 
			else {
				Logger.error("pourquoi une constante dans un struct?");
			}
		}
		//cas de field imbriqué
		else if(nwrecord instanceof FieldAccess fnwrec){
			fragment.append(rec(_factory,fnwrec));
			fragment.add(_factory.createLoadL(fa.getField().getOffset()));
			fragment.add(Library.IAdd);
		}
		//cas du flop
		else{
			Logger.error("L'acces des structs ont flop Oh no");
		}
		return fragment;
	}
}
