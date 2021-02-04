package com.bancoazteca.compras;

import com.bancoazteca.compras.empleados.business.ComprasEmpleadosBusiness;

public class ComprasEmpleados
{
	public static void main(String []args)
	{
		/**
		 * Necesita el archivo
		 * 		ComprasComercios_SIN_ICU.txt
		 * 
		 * Para poder generar el archivo de excel con las referencias a consultar
		 * 		ReferenciasAlnova.xls
		 * 
		 */
//		ComprasEmpleadosBusiness.generarArchivoReferencias();
		
		/**
		 * Necesita todos los reportes de conciliacion a leer en la ruta
		 */
		ComprasEmpleadosBusiness.generarReportesConciliacion();
		
		/**
		 * Necesita los archivos
		 * 		ComprasComercios_SIN_ICU.txt
		 * 		ReporteAlnova.txt
		 * 		EmpleadosParticipantes.txt
		 * 
		 * Para poder generar el archivo de excel con la respuesta
		 * 		ComprasEmpleadoSemana.xls
		 * 
		 */
//		ComprasEmpleadosBusiness.generaReporteComprasEmpleadosAlnova();
		
		
		/**
		 * En construccion
		 */
//		ComprasEmpleadosBusiness.generaReporteComprasEmpleadosConciliacion();
	}
}
