package com.bancoazteca.compras.empleados.entity;

/**
 * Objeto con la informacion de la compra
 * @author edgaronofrealvarez
 *
 */
public class InformacionCompra
{
	private String referencia;
	
	private String fechaCompra;
	
	private String monto;

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(String fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public String getMonto() {
		return monto;
	}

	public void setMonto(String monto) {
		this.monto = monto;
	}
}
