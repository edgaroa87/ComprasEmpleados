package com.bancoazteca.compras.empleados.entity;

/**
 * Objeto con la informacion del empleado participante
 * @author edgaronofrealvarez
 *
 */
public class EmpleadoParticipante
{
	private String nombreEmpleado;
	
	private String numeroEmpleado;
	
	private String alnova;
	
	private String icu;

	public String getNombreEmpleado() {
		return nombreEmpleado;
	}

	public void setNombreEmpleado(String nombreEmpleado) {
		this.nombreEmpleado = nombreEmpleado;
	}

	public String getNumeroEmpleado() {
		return numeroEmpleado;
	}

	public void setNumeroEmpleado(String numeroEmpleado) {
		this.numeroEmpleado = numeroEmpleado;
	}

	public String getAlnova() {
		return alnova;
	}

	public void setAlnova(String alnova) {
		this.alnova = alnova;
	}

	public String getIcu() {
		return icu;
	}

	public void setIcu(String icu) {
		this.icu = icu;
	}
}
