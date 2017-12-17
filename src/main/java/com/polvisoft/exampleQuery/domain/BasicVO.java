package com.polvisoft.exampleQuery.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BasicVO<PK> {

	public static final String PK = "pk";

	public BasicVO() {
		super();
	}

	public abstract PK getPk();

	public abstract void setPk(PK pk);

	@Override
	public String toString() {
		return "BasicVO [pk=" + this.getPk() + "]";
	}

	public abstract String toStringDebug();

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BasicVO) {
			BasicVO other = (BasicVO) obj;
			if (other.getPk().equals(this.getPk())) {
				return true;
			}
		}
		return super.equals(obj);
	}

}
