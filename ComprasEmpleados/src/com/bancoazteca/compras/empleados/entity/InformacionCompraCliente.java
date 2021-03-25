package com.bancoazteca.compras.empleados.entity;

import java.util.List;

/**
 * Informacion con la compra del cliente
 * @author edgaronofrealvarez
 *
 */
public class InformacionCompraCliente
{
	private String nombreCliente;
	
	private String clienteAlnova;
	
	private String icu;
	
	private int numeroCompras;
	
	private double montoComprasWalmart;
	
	private double montoComprasChedraui;
	
	private List<InformacionCompra> lstCompras;

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getClienteAlnova() {
		return clienteAlnova;
	}

	public void setClienteAlnova(String clienteAlnova) {
		this.clienteAlnova = clienteAlnova;
	}

	public String getIcu() {
		return icu;
	}

	public void setIcu(String icu) {
		this.icu = icu;
	}

	public int getNumeroCompras() {
		return numeroCompras;
	}

	public void setNumeroCompras(int numeroCompras) {
		this.numeroCompras = numeroCompras;
	}

	public double getMontoComprasWalmart() {
		return montoComprasWalmart;
	}

	public void setMontoComprasWalmart(double montoComprasWalmart) {
		this.montoComprasWalmart = montoComprasWalmart;
	}

	public double getMontoComprasChedraui() {
		return montoComprasChedraui;
	}

	public void setMontoComprasChedraui(double montoComprasChedraui) {
		this.montoComprasChedraui = montoComprasChedraui;
	}

	public List<InformacionCompra> getLstCompras() {
		return lstCompras;
	}

	public void setLstCompras(List<InformacionCompra> lstCompras) {
		this.lstCompras = lstCompras;
	}
}
