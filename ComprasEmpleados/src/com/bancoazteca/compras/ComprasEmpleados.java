package com.bancoazteca.compras;

import com.bancoazteca.compras.empleados.business.ComprasEmpleadosBusiness;

public class ComprasEmpleados
{
	public static void main(String []args)
	{
		/**
		 * Metodo para generar el archivo con la informacion de las compras reportadas en los archivos de conciliacion que se comparten a Alnoca
		 * es necesario depositar el o los archivos de conciliacion de los que se desea obtener el reporte
		 * 
		 * Ruta - /Users/edgaronofrealvarez/Desktop/ReporteCompras/Conciliaciones/
		 * 
		 * Genera el archivo de excel con las referencias a consultar en
		 * 		Archivo -> ReferenciasAlnova.xls / Referencias_CampaniaIphone.xls / Referencias_CuponAmazon.xls
		 * 		Ruta -> /Users/edgaronofrealvarez/Desktop/ReporteCompras/Archivos Generados/
		 */
//		ComprasEmpleadosBusiness.generarArchivoReferenciasAlnova(1); //Iphone
		
//		ComprasEmpleadosBusiness.generarArchivoReferenciasAlnova(2); //Amazon
		
		
		
		/**
		 * Metodo para generar el archivo con la informacion de las compras reportadas en los archivos de conciliacion
		 * es necesario depositar el o los archivos de conciliacion de los que se desea obtener el reporte
		 * 
		 * Ruta - /Users/edgaronofrealvarez/Desktop/ReporteCompras/Conciliaciones/
		 * 
		 * Genera el archivo de excel con las compras registradas en
		 * 		Archivo -> ReporteCompras.xls
		 * 		Ruta -> /Users/edgaronofrealvarez/Desktop/ReporteCompras/Archivos Generados/
		 * 
		 */
//		ComprasEmpleadosBusiness.generarReportesConciliacion();
		
		
		
		/**
		 * Metodo para generar el reporte de los empleados que realizaron compras en Supermercados, necesita los siguiente archivos en:
		 * 		Archivos de conciliacion /Users/edgaronofrealvarez/Desktop/ReporteCompras/Conciliaciones/
		 * 		ReporteAlnova.txt en /Users/edgaronofrealvarez/Desktop/ReporteCompras/Concurso/
		 * 		EmpleadosParticipantes.txt en /Users/edgaronofrealvarez/Desktop/ReporteCompras/Concurso/
		 * 
		 * 
		 *Genera el archivo de excel con la respuesta en:
		 * 		Archivo -> ComprasEmpleadoSemana.xls
		 * 		Ruta -> /Users/edgaronofrealvarez/Desktop/ReporteCompras/Archivos Generados/
		 * 
		 */
		ComprasEmpleadosBusiness.generaReporteComprasEmpleadosAlnova();
		
		
		
		/**
		 * Metodo para generar el reporte de los empleados que realizaron compras en Supermercados, necesita los siguiente archivos en:
		 * 		Archivos de conciliacion /Users/edgaronofrealvarez/Desktop/ReporteCompras/Conciliaciones/
		 * 		EmpleadosParticipantes.txt en /Users/edgaronofrealvarez/Desktop/ReporteCompras/Concurso/
		 * 
		 * 
		 *Genera el archivo de excel con la respuesta en:
		 * 		Archivo -> ComprasEmpleadoSemana.xls
		 * 		Ruta -> /Users/edgaronofrealvarez/Desktop/ReporteCompras/Archivos Generados/
		 * 
		 */
//		 ComprasEmpleadosBusiness.generaReporteComprasEmpleadosConciliacion();
		
		
		
		/**
		 * Metodo para generar el reporte por compras de clientes en los comercios de Chedraui/Walmart, no participan empleados y require
		 * los archivos de:
		 * 
		 * 		Archivos de conciliacion /Users/edgaronofrealvarez/Desktop/ReporteCompras/Conciliaciones/
		 * 		ReporteAlnova.txt en /Users/edgaronofrealvarez/Desktop/ReporteCompras/Concurso/
		 * 		ClientesBancaSinEmpleados.txt en /Users/edgaronofrealvarez/Desktop/ReporteCompras/Concurso/
		 *
		 *Genera el archivo de excel con la respuesta en:
		 * 		Archivo -> ComprasClientes_XXXXXXX.xls
		 * 		Ruta -> /Users/edgaronofrealvarez/Desktop/ReporteCompras/Archivos Generados/
		 *
		 */
//		ComprasEmpleadosBusiness.generarReportePorMontosDeComprasClientes();
	}
}
