package fr.ensimag.deca.context;


/**
 * Definition of an operator like add, substract, multiply, divide etc.
 *
 * @author gl07
 * @date 13/01/2020
 */
public class Operator {
	
	private String name; // the operator name

	public Operator(String name){
		this.name = name;
	}


	/**
	 * return the hashcode of an operator
	 * @return the hashcode
	 **/
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}


	 /**
	 * To compare two operator
	 * @param obj the operator
	 * @return if two operator are the same, return true
	 **/
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Operator other = (Operator) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}


	/**
	 * Transform the operator to string
	 * @return string
	 **/
	@Override
	public String toString() {
		return  this.getName();
	}


	/**
	 * check if the operator is  +
	 * @return boolean
	 **/
	public boolean isPlus() {
		return this.name.equals("+");
	}

	/**
	 * check if the operator is -
	 * @return boolean
	 **/
	public boolean isMinus() {
		return this.name.equals("-");
	}

	/**
	 * check if the operator is *
	 * @return boolean
	 **/
	public boolean isMult() {
		return this.name.equals("*");
	}

	/**
	 * check if the operator is /
	 * @return boolean
	 **/
	public boolean isDivide() {
		return this.name.equals("/");
	}

	/**
	 * check if the operator is %
	 * @return boolean
	 **/
	public boolean isMod() {
		return this.name.equals("%");
	}

	/**
	 * check if the operator is ==
	 * @return boolean
	 **/
	public boolean isEq() {
		return this.name.equals("==");
	}

	/**
	 * check if the operator is !=
	 * @return boolean
	 **/
	public boolean isNeq() {
		return this.name.equals("!=");
	}

	/**
	 * check if the operator is <
	 * @return boolean
	 **/
	public boolean isLt() {
		return this.name.equals("<");
	}

	/**
	 * check if the operator is >
	 * @return boolean
	 **/
	public boolean isGt() {
		return this.name.equals(">");
	}

	/**
	 * check if the operator is <=
	 * @return boolean
	 **/
	public boolean isLeq() {
		return this.name.equals("<=");
	}

	/**
	 * check if the operator is >=
	 * @return boolean
	 **/
	public boolean isGeq() {
		return this.name.equals(">=");
	}

	/**
	 * check if the operator is &&
	 * @return boolean
	 **/
	public boolean isAnd() {
		return this.name.equals("&&");
	}

	/**
	 * check if the operator is ||
	 * @return boolean
	 **/
	public boolean isOr() {
		return this.name.equals("||");
	}

	/**
	 * check if the operator is !
	 * @return boolean
	 **/
	public boolean isNot() {
		return this.name.equals("!");
	}
	/**
	 * Return the name of the operatos
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
