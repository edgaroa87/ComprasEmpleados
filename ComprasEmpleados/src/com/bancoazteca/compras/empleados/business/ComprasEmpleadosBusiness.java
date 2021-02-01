package com.bancoazteca.compras.empleados.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.bancoazteca.compras.empleados.entity.ArchivoAlnova;
import com.bancoazteca.compras.empleados.entity.EmpleadoParticipante;
import com.bancoazteca.compras.empleados.entity.InformacionCompra;
import com.bancoazteca.compras.empleados.entity.InformacionCompraConciliacionSinICU;
import com.bancoazteca.compras.empleados.entity.InformacionCompraEmpleado;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ComprasEmpleadosBusiness
{
	private final static String RUTA_ARCHIVOS="/Users/edgaronofrealvarez/Desktop/ReporteCompras/";
	private final static String ARCHIVO_REPORTE_REFERENCIAS_ALNOVA="ReferenciasAlnova.xls";
	private final static String ARCHIVO_REPORTE_ALNOVA="ReporteAlnova.txt";
	private final static String ARCHIVO_EMPLEADOS_PARTICIPANTES="EmpleadosParticipantes.txt";
	private final static String ARCHIVO_COMPRA_COMERCIOS_SIN_ICU="ComprasComercios_SIN_ICU.txt";
	private final static String ARCHIVO_COMPRA_EMPLEADOS="ComprasEmpleados_";
	private final static String DELIMITADOR="\\|";
	
	
	/**
	 * Metodo para consultar/crear el archivo excel con las referencias a consultar por Alnova
	 */
	public static void generarArchivoReferencias()
	{
		System.out.println("Generando archivo con referencias para Alnova...");
		List<InformacionCompraConciliacionSinICU> lstArchivoComprasConciliacion=leerArchivoConciliacionSINICU();
		System.out.println("Compras en archivo Conciliacion... ".concat(String.valueOf(lstArchivoComprasConciliacion.size())));
		generaArchivoExcelReferencias(lstArchivoComprasConciliacion);
	}
	
	/**
	 * Metodo para generar el reporte de las compras de los empleados por el reporte Alnova
	 */
	public static void generaReporteComprasEmpleadosAlnova()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<InformacionCompraEmpleado> lstComprasEmpleado=new ArrayList<>();
		
		System.out.println("Generando Archivo de compras de empleado con Archivo Alnova...");
		List<InformacionCompraConciliacionSinICU> lstArchivoComprasConciliacion=leerArchivoConciliacionSINICU();
		System.out.println("\nCompras en archivo Conciliacion... ".concat(String.valueOf(lstArchivoComprasConciliacion.size())));
		List<ArchivoAlnova> lstArchivoAlnova=leerArchivoAlnova();
		System.out.println("Registros en archivo Alnova... ".concat(String.valueOf(lstArchivoAlnova.size())));
		List<EmpleadoParticipante> lstEmpleadosParticipantes=leerArchivoEmpleadosParticipantes();
		System.out.println("Empleados participantes... ".concat(String.valueOf(lstEmpleadosParticipantes.size())));
		
		
		System.out.println("\nIterando información de Alnova...");
		for(ArchivoAlnova informacionAlnova:lstArchivoAlnova) {
			
			//Se busca que el empleado exista en la lista de los empleados participantes
			EmpleadoParticipante empleadoParticipante=lstEmpleadosParticipantes.stream().filter(empleado -> informacionAlnova.getAlnova().equals(empleado.getAlnova())).findAny().orElse(null);
			
			if(empleadoParticipante!=null) {
				
				//Se busca informacion de la compra en conciliacion
				InformacionCompraConciliacionSinICU informacionCompraConciliacion=lstArchivoComprasConciliacion.stream().filter(compraConciliacion -> informacionAlnova.getReferencia().equals(compraConciliacion.getReferencia())).findAny().orElse(null);
				
				if(informacionCompraConciliacion!=null) {
					
					//Se busca el objeto con la informacion del empleado y sus compras para actualizacion
					InformacionCompraEmpleado informacionCompraEmpleado=lstComprasEmpleado.stream().filter(compraEmpleado -> empleadoParticipante.getNumeroEmpleado().equals(compraEmpleado.getNumeroEmpleado())).findAny().orElse(null);
					
					if(informacionCompraEmpleado!=null) {
						
						System.out.println("\nSe actualizara la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
						
						//Se remueve informacion del empleado
						lstComprasEmpleado.remove(informacionCompraEmpleado);
						
						//Se suma 1 compra al registro
						informacionCompraEmpleado.setNumeroCompras(informacionCompraEmpleado.getNumeroCompras()+1);
						
						//Se agrega informacion de la nueva compra encontrada
						InformacionCompra informacionCompra=new InformacionCompra();
						informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
						informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
						informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
						
						//Se agrega nueva compra a la lista
						informacionCompraEmpleado.getLstCompras().add(informacionCompra);
						
						//Se inserta informacion del empleado a la lista
						lstComprasEmpleado.add(informacionCompraEmpleado);
						
						System.out.println("Se actualizo la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
					}
					else {
						
						//Se crea el objeto para el empleado
						informacionCompraEmpleado=new InformacionCompraEmpleado();
						
						//Se setea la informacion del empleado
						informacionCompraEmpleado.setNombreEmpleado(empleadoParticipante.getNombreEmpleado());
						informacionCompraEmpleado.setNumeroCompras(1);
						informacionCompraEmpleado.setNumeroEmpleado(empleadoParticipante.getNumeroEmpleado());
						
						//Se agrega informacion de la nueva compra encontrada
						List<InformacionCompra> lstCompras=new ArrayList<>();
						InformacionCompra informacionCompra=new InformacionCompra();
						informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
						informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
						informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
						lstCompras.add(informacionCompra);
						
						//Se agrega nueva compra a la lista
						informacionCompraEmpleado.setLstCompras(lstCompras);
						
						//Se inserta informacion del empleado a la lista
						lstComprasEmpleado.add(informacionCompraEmpleado);
						
						System.out.println("\nSe agrego la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
					}
				}
				else
					System.out.println("\nNo existe informacion de la referencia {".concat(informacionAlnova.getReferencia()).concat("} en el archivo de conciliacion.."));
			}
			else
				System.out.println("\nEl empleado con Alnova {".concat(informacionAlnova.getAlnova()).concat("} no participa en el concurso..."));
		}
		
		String informacionJson=gson.toJson(lstComprasEmpleado);
		System.out.println("\n\nInformacion de compras...\n".concat(informacionJson));
	}
	
	/**
	 * Metodo para generar el reporte de las compras de los empleados por el reporte de Conciliacion
	 */
	public static void generaReporteComprasEmpleadosConciliacion()
	{
		
	}
	
	/**
	 * Metodo para crear el archivo de excel con la referencias para Alnova
	 * @param List<InformacionCompraConciliacionSinICU>
	 */
	@SuppressWarnings("deprecation")
	private static void generaArchivoExcelReferencias(List<InformacionCompraConciliacionSinICU> lstArchivoComprasConciliacion)
	{
		int filaIndice=0;
		File archivo = new File(RUTA_ARCHIVOS.concat(ARCHIVO_REPORTE_REFERENCIAS_ALNOVA));
		HSSFWorkbook libroExcel = new HSSFWorkbook();
		HSSFSheet hoja1 = libroExcel.createSheet("Referencias Compras");
		HSSFRow fila = hoja1.createRow(filaIndice);
		HSSFCell celda = fila.createCell((short) 0);
		celda.setCellValue("Referencia");
		HSSFCell celda2 = fila.createCell((short) 1);
		celda2.setCellValue("Cliente Alnova");
		for(InformacionCompraConciliacionSinICU compra: lstArchivoComprasConciliacion) {
			filaIndice++;
			fila=hoja1.createRow(filaIndice);
			HSSFCell celdaReferencia = fila.createCell((short) 0);
			celdaReferencia.setCellValue(compra.getReferencia());
		}
		try {
			FileOutputStream salida = new FileOutputStream(archivo);
			libroExcel.write(salida);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo para leer el archivo de conciliacion sin el ICU del cliente
	 * @return  List<InformacionCompraConciliacionSinICU>
	 */
	private static List<InformacionCompraConciliacionSinICU> leerArchivoConciliacionSINICU()
	{
		List<InformacionCompraConciliacionSinICU> lstArchivoComprasConciliacion=new ArrayList<>();
		
		try {
			Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS.concat(ARCHIVO_COMPRA_COMERCIOS_SIN_ICU)));
			while (myReader.hasNextLine()) {
				String[] informacionLinea = myReader.nextLine().split(DELIMITADOR);
				InformacionCompraConciliacionSinICU informacionCompraConciliacionSinICU=new InformacionCompraConciliacionSinICU();
				informacionCompraConciliacionSinICU.setIdAgente(informacionLinea[0]);
				informacionCompraConciliacionSinICU.setDescripcionAgenteCompra(informacionLinea[1]);
				informacionCompraConciliacionSinICU.setIdCanalCompra(informacionLinea[2]);
				informacionCompraConciliacionSinICU.setDescripcionCanalCompra(informacionLinea[3]);
				informacionCompraConciliacionSinICU.setReferencia(informacionLinea[4]);
				informacionCompraConciliacionSinICU.setIdSusidiaria(informacionLinea[5]);
				informacionCompraConciliacionSinICU.setDescripcionSubsidiaria(informacionLinea[6]);
				informacionCompraConciliacionSinICU.setFechaHoraCompra(informacionLinea[7]);
				informacionCompraConciliacionSinICU.setNumeroAutorizacion(informacionLinea[8]);
				informacionCompraConciliacionSinICU.setMonto(informacionLinea[9]);
				informacionCompraConciliacionSinICU.setComisionCliente(informacionLinea[10]);
				informacionCompraConciliacionSinICU.setIva(informacionLinea[11]);
				informacionCompraConciliacionSinICU.setComisionAgente(informacionLinea[12]);
				informacionCompraConciliacionSinICU.setIvaComisionAgente(informacionLinea[13]);
				informacionCompraConciliacionSinICU.setEstatusCompra(informacionLinea[14]);
				informacionCompraConciliacionSinICU.setIdTienda(informacionLinea[15]);
				informacionCompraConciliacionSinICU.setIdCajero(informacionLinea[16]);
				informacionCompraConciliacionSinICU.setIdCaja(informacionLinea[17]);
				lstArchivoComprasConciliacion.add(informacionCompraConciliacionSinICU);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return lstArchivoComprasConciliacion;
	}
	
	/**
	 * Metodo para la lectura del archivo Alnova
	 * @return List<ArchivoAlnova>
	 */
	private static List<ArchivoAlnova> leerArchivoAlnova()
	{
		List<ArchivoAlnova> lstArchivoAlnova=new ArrayList<>();
		
		try {
			Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS.concat(ARCHIVO_REPORTE_ALNOVA)));
			while (myReader.hasNextLine()) {
				String[] informacionLinea = myReader.nextLine().split(DELIMITADOR);
				ArchivoAlnova archivoAlnova=new ArchivoAlnova();
				archivoAlnova.setReferencia(informacionLinea[0]);
				archivoAlnova.setAlnova(informacionLinea[1]);
				lstArchivoAlnova.add(archivoAlnova);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return lstArchivoAlnova;
	}
	
	/**
	 * Metodo para obtener la lista de los empleados participantes
	 * @return List<EmpleadoParticipante>
	 */
	private static List<EmpleadoParticipante> leerArchivoEmpleadosParticipantes()
	{
		List<EmpleadoParticipante> lstEmpleadosParticipantes=new ArrayList<>();
		
		try {
			Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS.concat(ARCHIVO_EMPLEADOS_PARTICIPANTES)));
			while (myReader.hasNextLine()) {
				String[] informacionLinea = myReader.nextLine().split(DELIMITADOR);
				EmpleadoParticipante empleadoParticipante=new EmpleadoParticipante();
				empleadoParticipante.setNombreEmpleado(informacionLinea[0]);
				empleadoParticipante.setNumeroEmpleado(informacionLinea[1]);
				empleadoParticipante.setAlnova(informacionLinea[2]);
				empleadoParticipante.setIcu(informacionLinea[3]);
				lstEmpleadosParticipantes.add(empleadoParticipante);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return lstEmpleadosParticipantes;
	}
}


/**
 * Calendar calendar = Calendar.getInstance(locale); 
calendar.set(year, month, day); 
int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
**/
