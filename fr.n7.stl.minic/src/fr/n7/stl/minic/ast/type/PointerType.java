/**
 * 
 */
package fr.n7.stl.minic.ast.type;

import java.util.Iterator;

import fr.n7.stl.minic.ast.scope.Declaration;
import fr.n7.stl.minic.ast.scope.HierarchicalScope;
import fr.n7.stl.util.Logger;

/**
 * Implementation of the Abstract Syntax Tree node for a pointer type.
 * @author Marc Pantel
 *
 */
public class PointerType implements Type {

	protected Type element;

	public PointerType(Type _element) {
		this.element = _element;
	}
	
	public Type getPointedType() {
		return this.element;
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#equalsTo(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean equalsTo(Type _other) {
		if(_other instanceof PointerType ptr){
			return this.element.equalsTo(ptr.getPointedType());
		} else {
			return this.element.equalsTo(_other);
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#compatibleWith(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public boolean compatibleWith(Type _other) {
		if (_other instanceof NamedType) {
			return this.compatibleWith(((NamedType) _other).getType());
		} else {
			return this.element.compatibleWith(_other);
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#merge(fr.n7.stl.block.ast.Type)
	 */
	@Override
	public Type merge(Type _other) {
		if(_other instanceof PointerType ptr){
			return new PointerType(this.element.merge(ptr.getPointedType()));
		} else {
			Logger.error("Type non respecté pour un merge de pointeur");
			return AtomicType.NullType;
		}
	}

	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.Type#length(int)
	 */
	@Override
	public int length() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(" + this.element + " *)";
	}
	
	/* (non-Javadoc)
	 * @see fr.n7.stl.block.ast.type.Type#resolve(fr.n7.stl.block.ast.scope.Scope)
	 */
	@Override
	public boolean completeResolve(HierarchicalScope<Declaration> _scope) {
		return this.element.completeResolve(_scope);
	}

}
