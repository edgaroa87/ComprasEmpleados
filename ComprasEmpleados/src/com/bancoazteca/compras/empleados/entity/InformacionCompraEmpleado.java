package com.bancoazteca.compras.empleados.entity;

import java.util.List;

/**
 * Objeto con la informacion de la compra del empleado
 * @author edgaronofrealvarez
 *
 */
public class InformacionCompraEmpleado
{
	private String nombreEmpleado;
	
	private String numeroEmpleado;
	
	private int numeroCompras;
	
	private List<InformacionCompra> lstCompras;

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

	public int getNumeroCompras() {
		return numeroCompras;
	}

	public void setNumeroCompras(int numeroCompras) {
		this.numeroCompras = numeroCompras;
	}

	public List<InformacionCompra> getLstCompras() {
		return lstCompras;
	}

	public void setLstCompras(List<InformacionCompra> lstCompras) {
		this.lstCompras = lstCompras;
	}
}
