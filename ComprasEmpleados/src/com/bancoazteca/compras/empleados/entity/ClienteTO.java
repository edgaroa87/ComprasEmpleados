package com.bancoazteca.compras.empleados.entity;

/**
 * Objeto con la informacion del cliente
 * @author edgaronofrealvarez
 *
 */
public class ClienteTO
{
	private String nombreCliente;
		
	private String alnova;
	
	private String icu;

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
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
