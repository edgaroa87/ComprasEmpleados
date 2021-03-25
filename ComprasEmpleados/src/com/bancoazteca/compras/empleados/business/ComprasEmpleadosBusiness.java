package com.bancoazteca.compras.empleados.business;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.bancoazteca.compras.empleados.entity.ArchivoAlnova;
import com.bancoazteca.compras.empleados.entity.ClienteTO;
import com.bancoazteca.compras.empleados.entity.EmpleadoParticipante;
import com.bancoazteca.compras.empleados.entity.InformacionCompra;
import com.bancoazteca.compras.empleados.entity.InformacionCompraCliente;
import com.bancoazteca.compras.empleados.entity.InformacionCompraConciliacion;
import com.bancoazteca.compras.empleados.entity.InformacionCompraEmpleado;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ComprasEmpleadosBusiness
{
	private final static Logger LOG=Logger.getLogger(ComprasEmpleadosBusiness.class);
	
	private final static String RUTA_ARCHIVOS_CONCURSO="/Users/edgaronofrealvarez/Desktop/ReporteCompras/Concurso/";
	private final static String RUTA_ARCHIVOS_CONCILIACIONES="/Users/edgaronofrealvarez/Desktop/ReporteCompras/Conciliaciones/";
	private final static String RUTA_ARCHIVOS_GENERADOS="/Users/edgaronofrealvarez/Desktop/ReporteCompras/Archivos Generados/";
	private final static String ARCHIVO_REPORTE_REFERENCIAS_ALNOVA="ReferenciasAlnova.xls";
	private final static String ARCHIVO_REPORTE_REFERENCIAS_IPHONE="Referencias_CampaniaIphone.xls";
	private final static String ARCHIVO_REPORTE_REFERENCIAS_CUPON_AMAZON="Referencias_CuponAmazon.xls";
	private final static String ARCHIVO_REPORTE_ALNOVA="ReporteAlnova.txt";
	private final static String ARCHIVO_EMPLEADOS_PARTICIPANTES="EmpleadosParticipantes.txt";
	private final static String ARCHIVO_CLIENTES_SIN_EMPLEADOS="ClientesBancaSinEmpleados.txt";
	private final static String ARCHIVO_COMPRA_EMPLEADOS="ComprasEmpleados_";
	private final static String ARCHIVO_COMPRA_CLIENTES="ComprasClientes_Semana-";
	private final static String ARCHIVO_REPORTE_COMPRAS="ReporteCompras.xls";
	private final static String ARCHIVO_REPORTERIA_ALNOVA="BAT1SBAS.COMPRA.REFERENC.D";
	private final static String ARCHIVO_EXCEL_EXTENSION=".xls";
	private final static String DELIMITADOR_PIPE="\\|";
	private final static String DELIMITADOR_PIPE_EXCEL="|";
	private final static String DELIMITADOR_GUION_MEDIO="-";
	private final static String DELIMITADOR_SALTO_LINEA="\n";
	private final static String DELIMITADOR_ESPACIO=" ";
	private final static String DELIMITADOR_CADENA_VACIA="";
	
	private final static String HOJA_REPORTE_COMPRAS="Reporte compras";
	private final static String HOJA_REFERENCIAS_WALMART="Referencias Walmart";
	private final static String HOJA_REFERENCIAS_CHEDRAUI="Referencias Chedraui";
	private final static String HOJA_REFERENCIAS_NETO="Referencias Neto";
	
	private final static String COLUMNA_REFERENCIA="Referencia";
	private final static String COLUMNA_COMERCIO="Comercio";
	private final static String COLUMNA_ALNOVA="Cliente Alnova";
	private final static String COLUMNA_NEGOCIO="Negocio";
	private final static String COLUMNA_MONTO="Monto";
	private final static String COLUMNA_FECHA="Fecha";
	private final static String COLUMNA_HORA="Hora";
	
	private final static String ID_COMERCIO_CHEDRAUI="49";
	private final static String COMERCIO_CHEDRAUI="Chedraui";
	private final static String ID_COMERCIO_WALMART="33";
	private final static String COMERCIO_WALMART="Walmart";
	private final static String ID_COMERCIO_NETO="23";
	private final static String COMERCIO_NETO="Neto";
	private final static String ESTATUS_REVERSO="2";
	private final static String ESTATUS_OK="1";
	
	private final static String TRUE="true";
	
	private final static int CAMPANIA_IPHONE=1;
	private final static int CAMPANIA_CUPON_AMAZON=2;
	
	/**
	 * Metodo para generar el archivo excel con las referencias a consultar por Alnova
	 */
	public static void generarArchivoReferenciasAlnova(int campania)
	{
		LOG.info("Se ha solicitado la generacion del archivo con las Referencias para Alnova...");
		List<File> lstArchivosConciliacion=Arrays.asList(new File(RUTA_ARCHIVOS_CONCILIACIONES).listFiles());
		Collections.sort(lstArchivosConciliacion);
		List<InformacionCompraConciliacion> lstCompras=leerArchivosConciliacionesSINICU(lstArchivosConciliacion);
		LOG.info("Compras en archivo(s) de conciliacion... ".concat(String.valueOf(lstCompras.size())));
		generaArchivoExcelReferenciasAlnova(lstCompras, campania);
		generarArchivoReporteriaAlnova(lstCompras);
	}
	
	/**
	 * Metodo para generar el archivo excel con el reporte de las compras reportadas en los archivos de conciliacion
	 */
	public static void generarReportesConciliacion()
	{
		LOG.info("Se ha solicitado la generacion del archivo con las compras reportadas en los archivos de conciliacion...");
		List<File> lstArchivosConciliacion=Arrays.asList(new File(RUTA_ARCHIVOS_CONCILIACIONES).listFiles());
		Collections.sort(lstArchivosConciliacion);
		List<InformacionCompraConciliacion> lstCompras=leerArchivosConciliacionesSINICU(lstArchivosConciliacion);
		LOG.info("Compras en archivo(s) de conciliacion... ".concat(String.valueOf(lstCompras.size())));
		generarReportesCompras(lstCompras);
	}
	
	/**
	 * Metodo para generar el reporte de las compras de los empleados por el reporte Alnova
	 */
	public static void generaReporteComprasEmpleadosAlnova()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<InformacionCompraEmpleado> lstComprasEmpleado=new ArrayList<>();
		
		LOG.info("Generando Archivo de compras empleados con archivo Alnova...");
		LOG.info("Leyendo compras de archivos de conciliacion...");
		List<File> lstArchivosConciliacion=Arrays.asList(new File(RUTA_ARCHIVOS_CONCILIACIONES).listFiles());
		Collections.sort(lstArchivosConciliacion);
		List<InformacionCompraConciliacion> lstArchivoComprasConciliacion=leerArchivosConciliacionesSINICU(lstArchivosConciliacion);
		LOG.info("Compras en archivo Conciliacion... ".concat(String.valueOf(lstArchivoComprasConciliacion.size())));
		List<ArchivoAlnova> lstArchivoAlnova=leerArchivoAlnova();
		LOG.info("Registros en archivo Alnova... ".concat(String.valueOf(lstArchivoAlnova.size())));
		List<EmpleadoParticipante> lstEmpleadosParticipantes=leerArchivoEmpleadosParticipantes();
		LOG.info("Empleados participantes... ".concat(String.valueOf(lstEmpleadosParticipantes.size())));
		
		
		LOG.info("Iterando informacion del archivo de Alnova...");
		for(ArchivoAlnova informacionAlnova:lstArchivoAlnova) {
			
			//Se busca que el empleado exista en la lista de los empleados participantes
			EmpleadoParticipante empleadoParticipante=lstEmpleadosParticipantes.stream().filter(empleado -> informacionAlnova.getAlnova().equals(empleado.getAlnova())).findAny().orElse(null);
			
			if(empleadoParticipante!=null) {
				
				//Se busca informacion de la compra en conciliacion
				InformacionCompraConciliacion informacionCompraConciliacion=lstArchivoComprasConciliacion.stream().filter(compraConciliacion -> informacionAlnova.getReferencia().equals(compraConciliacion.getReferencia())).findAny().orElse(null);
				
				if(informacionCompraConciliacion!=null) {
					
					//Se busca el objeto con la informacion del empleado y sus compras para actualizacion
					InformacionCompraEmpleado informacionCompraEmpleado=lstComprasEmpleado.stream().filter(compraEmpleado -> empleadoParticipante.getNumeroEmpleado().equals(compraEmpleado.getNumeroEmpleado())).findAny().orElse(null);
					
					if(informacionCompraEmpleado!=null) {
						
						LOG.info("Se actualizara la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
						
						//Se remueve informacion del empleado
						lstComprasEmpleado.remove(informacionCompraEmpleado);
						
						//Se suma 1 compra al registro
						informacionCompraEmpleado.setNumeroCompras(informacionCompraEmpleado.getNumeroCompras()+1);
						
						//Se agrega informacion de la nueva compra encontrada
						InformacionCompra informacionCompra=new InformacionCompra();
						informacionCompra.setComproEn(comercio(informacionCompraConciliacion.getIdAgente()));
						informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
						informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
						informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
						
						//Se agrega nueva compra a la lista
						informacionCompraEmpleado.getLstCompras().add(informacionCompra);
						
						//Se inserta informacion del empleado a la lista
						lstComprasEmpleado.add(informacionCompraEmpleado);
						
						LOG.info("Se actualizo la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
					}
					else {
						
						//Se crea el objeto para el empleado
						informacionCompraEmpleado=new InformacionCompraEmpleado();
						
						//Se setea la informacion del empleado
						informacionCompraEmpleado.setNombreEmpleado(empleadoParticipante.getNombreEmpleado());
						informacionCompraEmpleado.setNumeroCompras(1);
						informacionCompraEmpleado.setNumeroEmpleado(empleadoParticipante.getNumeroEmpleado());
						informacionCompraEmpleado.setIcu(empleadoParticipante.getIcu());
						
						//Se agrega informacion de la nueva compra encontrada
						List<InformacionCompra> lstCompras=new ArrayList<>();
						InformacionCompra informacionCompra=new InformacionCompra();
						informacionCompra.setComproEn(comercio(informacionCompraConciliacion.getIdAgente()));
						informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
						informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
						informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
						lstCompras.add(informacionCompra);
						
						//Se agrega nueva compra a la lista
						informacionCompraEmpleado.setLstCompras(lstCompras);
						
						//Se inserta informacion del empleado a la lista
						lstComprasEmpleado.add(informacionCompraEmpleado);
						
						LOG.info("Se agrego la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
					}
				}
				else
					LOG.info("No existe informacion de la referencia {".concat(informacionAlnova.getReferencia()).concat("} en el archivo de conciliacion.."));
			}
			else
				LOG.info("El empleado con Alnova {".concat(informacionAlnova.getAlnova()).concat("} no participa en el concurso..."));
		}
		
		String informacionJson=gson.toJson(lstComprasEmpleado);
		LOG.info("Informacion de compras...".concat(informacionJson));
		generarReporteCompras(lstComprasEmpleado);
	}
	
	/**
	 * Metodo para generar el reporte de las compras de los empleados por el reporte de Conciliacion con la informacion de ICU
	 * y si es empleado
	 */
	public static void generaReporteComprasEmpleadosConciliacion()
	{

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<InformacionCompraEmpleado> lstComprasEmpleado=new ArrayList<>();
		
		LOG.info("Generando Archivo de compras empleados con archivo Banca Digital...");
		LOG.info("Leyendo compras de archivos de conciliacion...");
		List<File> lstArchivosConciliacion=Arrays.asList(new File(RUTA_ARCHIVOS_CONCILIACIONES).listFiles());
		Collections.sort(lstArchivosConciliacion);
		List<InformacionCompraConciliacion> lstArchivoComprasConciliacion=leerArchivosConciliacionesConICU(lstArchivosConciliacion);
		List<EmpleadoParticipante> lstEmpleadosParticipantes=leerArchivoEmpleadosParticipantes();
		LOG.info("Empleados participantes... ".concat(String.valueOf(lstEmpleadosParticipantes.size())));
		
		
		LOG.info("Iterando informacion del archivo de conciliacion de Banca Digital...");
		for(InformacionCompraConciliacion informacionCompraConciliacion:lstArchivoComprasConciliacion) {
			
			//Se busca que el empleado exista en la lista de los empleados participantes
			EmpleadoParticipante empleadoParticipante=lstEmpleadosParticipantes.stream().filter(empleado -> informacionCompraConciliacion.getIcu().equals(empleado.getIcu())).findAny().orElse(null);
			
			if(empleadoParticipante!=null && informacionCompraConciliacion.getEstatusCompra().equalsIgnoreCase(ESTATUS_OK)) {
					
				//Se busca el objeto con la informacion del empleado y sus compras para actualizacion
				InformacionCompraEmpleado informacionCompraEmpleado=lstComprasEmpleado.stream().filter(compraEmpleado -> empleadoParticipante.getNumeroEmpleado().equals(compraEmpleado.getNumeroEmpleado())).findAny().orElse(null);
				
				if(informacionCompraEmpleado!=null) {
					
					LOG.info("Se actualizara la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
					
					//Se remueve informacion del empleado
					lstComprasEmpleado.remove(informacionCompraEmpleado);
					
					//Se suma 1 compra al registro
					informacionCompraEmpleado.setNumeroCompras(informacionCompraEmpleado.getNumeroCompras()+1);
					
					//Se agrega informacion de la nueva compra encontrada
					InformacionCompra informacionCompra=new InformacionCompra();
					informacionCompra.setComproEn(comercio(informacionCompraConciliacion.getIdAgente()));
					informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
					informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
					informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
					
					//Se agrega nueva compra a la lista
					informacionCompraEmpleado.getLstCompras().add(informacionCompra);
					
					//Se inserta informacion del empleado a la lista
					lstComprasEmpleado.add(informacionCompraEmpleado);
					
					LOG.info("Se actualizo la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
				}
				else {
					
					//Se crea el objeto para el empleado
					informacionCompraEmpleado=new InformacionCompraEmpleado();
					
					//Se setea la informacion del empleado
					informacionCompraEmpleado.setNombreEmpleado(empleadoParticipante.getNombreEmpleado());
					informacionCompraEmpleado.setNumeroCompras(1);
					informacionCompraEmpleado.setNumeroEmpleado(empleadoParticipante.getNumeroEmpleado());
					informacionCompraEmpleado.setIcu(empleadoParticipante.getIcu());
					
					//Se agrega informacion de la nueva compra encontrada
					List<InformacionCompra> lstCompras=new ArrayList<>();
					InformacionCompra informacionCompra=new InformacionCompra();
					informacionCompra.setComproEn(comercio(informacionCompraConciliacion.getIdAgente()));
					informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
					informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
					informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
					lstCompras.add(informacionCompra);
					
					//Se agrega nueva compra a la lista
					informacionCompraEmpleado.setLstCompras(lstCompras);
					
					//Se inserta informacion del empleado a la lista
					lstComprasEmpleado.add(informacionCompraEmpleado);
					
					LOG.info("Se agrego la informacion de compras para el empleado {".concat(informacionCompraEmpleado.getNumeroEmpleado()).concat("} ..."));
				}
			}
			else
				LOG.info("El empleado {".concat(informacionCompraConciliacion.getIcu()).concat("} no participa en el concurso o la compra fue reversada {").concat(informacionCompraConciliacion.getEstatusCompra()).concat("}..."));
		}
		
		String informacionJson=gson.toJson(lstComprasEmpleado);
		LOG.info("Informacion de compras...".concat(informacionJson));
		generarReporteCompras(lstComprasEmpleado);
	
	}
	
	/**
	 * Metodo para generar el reporte de compras por monto de la compra
	 */
	public static void generarReportePorMontosDeComprasClientes()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<InformacionCompraCliente> lstComprasClienteFiltradas=new ArrayList<>();
		List<InformacionCompraCliente> lstComprasCliente=new ArrayList<>();
		
		LOG.info("Generando Archivo de compras por monto...");
		
		LOG.info("Leyendo compras de archivos de conciliacion...");
		List<File> lstArchivosConciliacion=Arrays.asList(new File(RUTA_ARCHIVOS_CONCILIACIONES).listFiles());
		Collections.sort(lstArchivosConciliacion);
		List<InformacionCompraConciliacion> lstArchivoComprasConciliacion=leerArchivosConciliacionesSINICU(lstArchivosConciliacion);
		LOG.info("Compras en archivo Conciliacion... ".concat(String.valueOf(lstArchivoComprasConciliacion.size())));
		
		List<ArchivoAlnova> lstArchivoAlnova=leerArchivoAlnova();
		LOG.info("Registros en archivo Alnova... ".concat(String.valueOf(lstArchivoAlnova.size())));
		
		List<ClienteTO> lstClientes=leerArchivoClientes();
		LOG.info("Clientes Banco Azteca... ".concat(String.valueOf(lstClientes.size())));
		
		lstArchivoAlnova.forEach(compraAlnova -> {
			
			//Se busca que el empleado exista en la lista de los empleados participantes
			ClienteTO cliente=lstClientes.stream().filter(empleado -> compraAlnova.getAlnova().equals(empleado.getAlnova())).findAny().orElse(null);
			
			if(cliente!=null) {
				
				//Se busca informacion de la compra en conciliacion
				InformacionCompraConciliacion informacionCompraConciliacion=lstArchivoComprasConciliacion.stream().filter(compraConciliacion -> compraAlnova.getReferencia().equals(compraConciliacion.getReferencia())).findAny().orElse(null);
				
				if(informacionCompraConciliacion!=null && (informacionCompraConciliacion.getIdAgente().equals(ID_COMERCIO_CHEDRAUI) || informacionCompraConciliacion.getIdAgente().equals(ID_COMERCIO_WALMART))) {
					
					//Se busca el objeto con la informacion del cliente y sus compras para actualizacion
					InformacionCompraCliente informacionCompraCliente=lstComprasCliente.stream().filter(compraCliente -> cliente.getAlnova().equals(compraCliente.getClienteAlnova())).findAny().orElse(null);
					
					if(informacionCompraCliente!=null) {
						
						LOG.info("Se actualizara la informacion de compras para el cliente {".concat(informacionCompraCliente.getClienteAlnova()).concat("} ..."));
						
						//Se remueve informacion del empleado
						lstComprasCliente.remove(informacionCompraCliente);
						
						//Se suma 1 compra al registro
						informacionCompraCliente.setNumeroCompras(informacionCompraCliente.getNumeroCompras()+1);
						if(informacionCompraConciliacion.getIdAgente().equals(ID_COMERCIO_CHEDRAUI))
							informacionCompraCliente.setMontoComprasChedraui(informacionCompraCliente.getMontoComprasChedraui()+Double.valueOf(informacionCompraConciliacion.getMonto()));
						else
							informacionCompraCliente.setMontoComprasWalmart(informacionCompraCliente.getMontoComprasWalmart()+Double.valueOf(informacionCompraConciliacion.getMonto()));
						
						//Se agrega informacion de la nueva compra encontrada
						InformacionCompra informacionCompra=new InformacionCompra();
						informacionCompra.setComproEn(comercio(informacionCompraConciliacion.getIdAgente()));
						informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
						informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
						informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
						
						//Se agrega nueva compra a la lista
						informacionCompraCliente.getLstCompras().add(informacionCompra);
						
						//Se inserta informacion del empleado a la lista
						lstComprasCliente.add(informacionCompraCliente);
						
						LOG.info("Se actualizo la informacion de compras para el empleado {".concat(informacionCompraCliente.getClienteAlnova()).concat("} ..."));
					}
					else {
						
						//Se crea el objeto para el empleado
						informacionCompraCliente=new InformacionCompraCliente();
						
						//Se setea la informacion del empleado
						informacionCompraCliente.setNombreCliente(cliente.getNombreCliente());
						informacionCompraCliente.setNumeroCompras(1);
						if(informacionCompraConciliacion.getIdAgente().equals(ID_COMERCIO_CHEDRAUI))
							informacionCompraCliente.setMontoComprasChedraui(Double.valueOf(informacionCompraConciliacion.getMonto()));
						else
							informacionCompraCliente.setMontoComprasWalmart(Double.valueOf(informacionCompraConciliacion.getMonto()));
						informacionCompraCliente.setClienteAlnova(cliente.getAlnova());
						informacionCompraCliente.setIcu(cliente.getIcu());
						
						//Se agrega informacion de la nueva compra encontrada
						List<InformacionCompra> lstCompras=new ArrayList<>();
						InformacionCompra informacionCompra=new InformacionCompra();
						informacionCompra.setComproEn(comercio(informacionCompraConciliacion.getIdAgente()));
						informacionCompra.setReferencia(informacionCompraConciliacion.getReferencia());
						informacionCompra.setMonto(informacionCompraConciliacion.getMonto());
						informacionCompra.setFechaCompra(informacionCompraConciliacion.getFechaHoraCompra());
						lstCompras.add(informacionCompra);
						
						//Se agrega nueva compra a la lista
						informacionCompraCliente.setLstCompras(lstCompras);
						
						//Se inserta informacion del empleado a la lista
						lstComprasCliente.add(informacionCompraCliente);
						
						LOG.info("Se agrego la informacion de compras para el empleado {".concat(informacionCompraCliente.getClienteAlnova()).concat("} ..."));
					}
				}
				else
					LOG.info("No existe informacion de la referencia {".concat(compraAlnova.getReferencia()).concat("} en el archivo de conciliacion.."));
			}
			else
				LOG.info("Compra de empleado, no participa en el concurso...");
		});
		
		lstComprasCliente.forEach(compra -> {
			if(compra.getMontoComprasChedraui()>=1000 || compra.getMontoComprasWalmart()>=1500)
				lstComprasClienteFiltradas.add(compra);
		});
		
		String informacionJson=gson.toJson(lstComprasClienteFiltradas);
		LOG.info("Informacion de compras...".concat(informacionJson));
		generarReporteComprasMontoCliente(lstComprasClienteFiltradas);
	}
	
	/**
	 * Metodo para crear el archivo de excel con la referencias para Alnova
	 * @param List<InformacionCompraConciliacionSinICU>
	 */
	@SuppressWarnings("deprecation")
	private static void generaArchivoExcelReferenciasAlnova(List<InformacionCompraConciliacion> lstArchivoComprasConciliacion, int campania)
	{
		int filaIndiceWalmart=0;
		int filaIndiceChedraui=0;
		int filaIndiceNeto=0;
		String rutaArchivoReferenciasAlnova=RUTA_ARCHIVOS_GENERADOS.concat(campania==CAMPANIA_IPHONE ? ARCHIVO_REPORTE_REFERENCIAS_IPHONE:(
																		   campania== CAMPANIA_CUPON_AMAZON ? ARCHIVO_REPORTE_REFERENCIAS_CUPON_AMAZON:ARCHIVO_REPORTE_REFERENCIAS_ALNOVA));
		File archivo = new File(rutaArchivoReferenciasAlnova);
		HSSFWorkbook libroExcel = new HSSFWorkbook();
		
		LOG.info("Generando archivo con las referencias para Alnova...");
		//Crea la hoja para las referencias de Walmart
		HSSFSheet hojaWalmart = libroExcel.createSheet(HOJA_REFERENCIAS_WALMART);
		HSSFRow fila0W = hojaWalmart.createRow(filaIndiceWalmart);
		HSSFCell celdaRW = fila0W.createCell((short) 0);
		celdaRW.setCellValue(COLUMNA_REFERENCIA);
		HSSFCell celdaCW = fila0W.createCell((short) 1);
		celdaCW.setCellValue(COLUMNA_COMERCIO);
		HSSFCell celdaAW = fila0W.createCell((short) 2);
		celdaAW.setCellValue(COLUMNA_ALNOVA);
		
		//Crea la hoja para las referencas de Chedraui
		HSSFSheet hojaChedraui = libroExcel.createSheet(HOJA_REFERENCIAS_CHEDRAUI);
		HSSFRow fila0C = hojaChedraui.createRow(filaIndiceChedraui);
		HSSFCell celdaRC = fila0C.createCell((short) 0);
		celdaRC.setCellValue(COLUMNA_REFERENCIA);
		HSSFCell celdaCC = fila0C.createCell((short) 1);
		celdaCC.setCellValue(COLUMNA_COMERCIO);
		HSSFCell celdaAC = fila0C.createCell((short) 2);
		celdaAC.setCellValue(COLUMNA_ALNOVA);
		
		//Crea la hoja para las referencas de Neto
		HSSFSheet hojaNeto = libroExcel.createSheet(HOJA_REFERENCIAS_NETO);
		HSSFRow fila0N = hojaNeto.createRow(filaIndiceNeto);
		HSSFCell celdaRN = fila0N.createCell((short) 0);
		celdaRN.setCellValue(COLUMNA_REFERENCIA);
		HSSFCell celdaCN = fila0N.createCell((short) 1);
		celdaCN.setCellValue(COLUMNA_COMERCIO);
		HSSFCell celdaAN = fila0N.createCell((short) 2);
		celdaAN.setCellValue(COLUMNA_ALNOVA);
		
		for(InformacionCompraConciliacion compra: lstArchivoComprasConciliacion) {
			if(!compra.getEstatusCompra().equals(ESTATUS_REVERSO)) {
				switch(compra.getIdAgente()) {
					case ID_COMERCIO_WALMART:   filaIndiceWalmart++;
												HSSFRow filaWInfo=hojaWalmart.createRow(filaIndiceWalmart);
												HSSFCell celdaReferencia = filaWInfo.createCell((short) 0);
												celdaReferencia.setCellValue(compra.getReferencia());
												HSSFCell celdaComercio = filaWInfo.createCell((short) 1);
												celdaComercio.setCellValue(compra.getDescripcionAgenteCompra());
												break;
			
					case ID_COMERCIO_CHEDRAUI:  filaIndiceChedraui++;
												HSSFRow filaCInfo=hojaChedraui.createRow(filaIndiceChedraui);
												HSSFCell celdaReferenciaC = filaCInfo.createCell((short) 0);
												celdaReferenciaC.setCellValue(compra.getReferencia());
												HSSFCell celdaComercioC = filaCInfo.createCell((short) 1);
												celdaComercioC.setCellValue(compra.getDescripcionAgenteCompra());
												break;
											
					case ID_COMERCIO_NETO:		filaIndiceNeto++;
												HSSFRow filaNInfo=hojaNeto.createRow(filaIndiceNeto);
												HSSFCell celdaReferenciaN = filaNInfo.createCell((short) 0);
												celdaReferenciaN.setCellValue(compra.getReferencia());
												HSSFCell celdaComercioN = filaNInfo.createCell((short) 1);
												celdaComercioN.setCellValue(compra.getDescripcionAgenteCompra());
												break;
				}
			}
		}
		try {
			FileOutputStream salida = new FileOutputStream(archivo);
			libroExcel.write(salida);
			LOG.info("El archivo se creo en... ".concat(rutaArchivoReferenciasAlnova));
		} catch (IOException e) {
			LOG.error(mensajeError(e));
		}
	}
	
	/**
	 * Metodo para obtener la informacion de los reportes de conciliacion que se encuentren en la ruta indicada
	 * @param lstArchivos
	 * @return List<InformacionCompraConciliacionSinICU>
	 */
	@SuppressWarnings("resource")
	private static List<InformacionCompraConciliacion> leerArchivosConciliacionesSINICU(List<File> lstArchivos)
	{
		List<InformacionCompraConciliacion> lstArchivoComprasConciliacion=new ArrayList<>();
		
		try {
			for(File archivo:lstArchivos) {
				Scanner myReader = new Scanner(archivo);
				if(archivo.getName().contains(".txt")) {
					LOG.info("Leyendo archivo de conciliacion... ".concat(archivo.getName()));
					while (myReader.hasNextLine()) {
						String[] informacionLinea = myReader.nextLine().split(DELIMITADOR_PIPE);
						InformacionCompraConciliacion informacionCompraConciliacionSinICU=new InformacionCompraConciliacion();
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
				}
			}
		} catch (FileNotFoundException e) {
			LOG.error(mensajeError(e));
		}
		
		return lstArchivoComprasConciliacion;
	}
	
	/**
	 * Metodo para obtener la informacion de los reportes de conciliacion que se encuentren en la ruta indicada
	 * @param lstArchivos
	 * @return List<InformacionCompraConciliacionSinICU>
	 */
	@SuppressWarnings("resource")
	private static List<InformacionCompraConciliacion> leerArchivosConciliacionesConICU(List<File> lstArchivos)
	{
		List<InformacionCompraConciliacion> lstArchivoComprasConciliacion=new ArrayList<>();
		
		try {
			for(File archivo:lstArchivos) {
				Scanner myReader = new Scanner(archivo);
				if(archivo.getName().contains(".txt")) {
					LOG.info("Leyendo archivo de conciliacion... ".concat(archivo.getName()));
					while (myReader.hasNextLine()) {
						String[] informacionLinea = myReader.nextLine().split(DELIMITADOR_PIPE);
						InformacionCompraConciliacion informacionCompraConciliacionSinICU=new InformacionCompraConciliacion();
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
						informacionCompraConciliacionSinICU.setIcu(informacionLinea[18]);
						informacionCompraConciliacionSinICU.setEsEmpleado(informacionLinea[19].equalsIgnoreCase(TRUE) ? true:false);
						lstArchivoComprasConciliacion.add(informacionCompraConciliacionSinICU);
				    }
				}
			}
		} catch (FileNotFoundException e) {
			LOG.error(mensajeError(e));
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
			Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS_CONCURSO.concat(ARCHIVO_REPORTE_ALNOVA)));
			while (myReader.hasNextLine()) {
				String[] informacionLinea = myReader.nextLine().split(DELIMITADOR_PIPE);
				ArchivoAlnova archivoAlnova=new ArchivoAlnova();
				archivoAlnova.setReferencia(informacionLinea[0]);
				archivoAlnova.setAlnova(informacionLinea[1]);
				lstArchivoAlnova.add(archivoAlnova);
		    }
		} catch (FileNotFoundException e) {
			LOG.error(mensajeError(e));
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
			Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS_CONCURSO.concat(ARCHIVO_EMPLEADOS_PARTICIPANTES)));
			while (myReader.hasNextLine()) {
				String linea=myReader.nextLine();
				String[] informacionLinea = linea.split(DELIMITADOR_PIPE);
				if(informacionLinea.length>=4) {
					if(esInformado(informacionLinea[0]) && esInformado(informacionLinea[1]) 
						&& esInformado(informacionLinea[2]) && esInformado(informacionLinea[3])) {
						EmpleadoParticipante empleadoParticipante=new EmpleadoParticipante();
						empleadoParticipante.setNombreEmpleado(informacionLinea[0].toUpperCase());
						empleadoParticipante.setNumeroEmpleado(informacionLinea[1]);
						empleadoParticipante.setAlnova(informacionLinea[2]);
						empleadoParticipante.setIcu(informacionLinea[3]);
						lstEmpleadosParticipantes.add(empleadoParticipante);
					}else
						LOG.info("No se agrega el registro... ".concat(linea));
				}
				else
					LOG.info("No se agrega el registro... ".concat(linea));
		    }
		} catch (FileNotFoundException e) {
			LOG.error(mensajeError(e));
		}
		
		return lstEmpleadosParticipantes;
	}
	
	/**
	 * Metodo para obtener la lista de los empleados participantes
	 * @return List<EmpleadoParticipante>
	 */
	private static List<ClienteTO> leerArchivoClientes()
	{
		List<ClienteTO> lstEmpleadosParticipantes=new ArrayList<>();
		
		try {
			Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS_CONCURSO.concat(ARCHIVO_CLIENTES_SIN_EMPLEADOS)));
			while (myReader.hasNextLine()) {
				String linea=myReader.nextLine();
				String[] informacionLinea = linea.split(DELIMITADOR_PIPE);
				if(informacionLinea.length>=2) {
					if(esInformado(informacionLinea[0]) && esInformado(informacionLinea[1])) {
						ClienteTO empleadoParticipante=new ClienteTO();
						empleadoParticipante.setNombreCliente("N/A");
						empleadoParticipante.setAlnova(informacionLinea[1]);
						empleadoParticipante.setIcu(informacionLinea[0]);
						lstEmpleadosParticipantes.add(empleadoParticipante);
					}else
						LOG.info("No se agrega el registro... ".concat(linea));
				}
				else
					LOG.info("No se agrega el registro... ".concat(linea));
		    }
		} catch (FileNotFoundException e) {
			LOG.error(mensajeError(e));
		}
		
		return lstEmpleadosParticipantes;
	}
	
	/**
	 * Metodo para retornar el nombre del comercio en donde se hizo la compra
	 * @param idComercio
	 * @return String
	 */
	private static String comercio(String idComercio)
	{
		String comercio=COMERCIO_WALMART;
		switch(idComercio) {
			case ID_COMERCIO_WALMART: comercio=COMERCIO_WALMART;
						break;
						
			case ID_COMERCIO_CHEDRAUI: comercio=COMERCIO_CHEDRAUI;
						break;
						
			case ID_COMERCIO_NETO: comercio=COMERCIO_NETO;
						break;
		}
		
		return comercio;
	}
	
	/**
	 * Metodo para generar los archivos de salida por el procesamiento de la informacion
	 * @param lstComprasEmpleado
	 */
	@SuppressWarnings("deprecation")
	private static void generarReporteCompras(List<InformacionCompraEmpleado> lstComprasEmpleado)
	{
		Calendar sDateCalendar = new GregorianCalendar();

		String semana=String.valueOf(sDateCalendar.get(Calendar.WEEK_OF_YEAR)-1).concat(DELIMITADOR_GUION_MEDIO).concat(String.valueOf(sDateCalendar.get(Calendar.WEEK_OF_YEAR)));
		String archivoXls=RUTA_ARCHIVOS_GENERADOS.concat(ARCHIVO_COMPRA_EMPLEADOS).concat(semana).concat(ARCHIVO_EXCEL_EXTENSION);
		int filaIndice=0;
		File archivo = new File(archivoXls);
		HSSFWorkbook libroExcel = new HSSFWorkbook();
		HSSFSheet hoja1 = libroExcel.createSheet("Compras Empleados Semana ".concat(semana));
		HSSFRow fila = hoja1.createRow(filaIndice);
		HSSFCell celda = fila.createCell((short) 0);
		celda.setCellValue("Nombre empleado");
		HSSFCell celda2 = fila.createCell((short) 1);
		celda2.setCellValue("Numero empleado");
		HSSFCell celda3 = fila.createCell((short) 2);
		celda3.setCellValue("ICU");
		HSSFCell celda4 = fila.createCell((short) 3);
		celda4.setCellValue("Compras");
		HSSFCell celda5 = fila.createCell((short) 4);
		celda5.setCellValue("Total compras");
		
		for(InformacionCompraEmpleado compraEmpleado: lstComprasEmpleado) {
			filaIndice++;
			fila=hoja1.createRow(filaIndice);
			
			HSSFCell celdaReferencia = fila.createCell((short) 0);
			celdaReferencia.setCellValue(compraEmpleado.getNombreEmpleado());
			
			HSSFCell celdaNoEmpleado = fila.createCell((short) 1);
			celdaNoEmpleado.setCellValue(compraEmpleado.getNumeroEmpleado());
			
			HSSFCell celdaICU = fila.createCell((short) 2);
			celdaICU.setCellValue(compraEmpleado.getIcu());
			
			StringBuilder informacion=new StringBuilder();
			for(InformacionCompra infoCompra:compraEmpleado.getLstCompras()) {
				informacion.append(infoCompra.getComproEn()).append(DELIMITADOR_PIPE_EXCEL).append(infoCompra.getReferencia()).append(DELIMITADOR_PIPE_EXCEL).append(infoCompra.getFechaCompra()).append(DELIMITADOR_PIPE_EXCEL).append(infoCompra.getMonto()).append(DELIMITADOR_SALTO_LINEA);
			}
			
			HSSFCell celdaCompras = fila.createCell((short) 3);
			celdaCompras.setCellValue(informacion.toString());
			
			HSSFCell celdaTotalCompras = fila.createCell((short) 4);
			celdaTotalCompras.setCellValue(compraEmpleado.getNumeroCompras());
			
		}
		try {
			FileOutputStream salida = new FileOutputStream(archivo);
			libroExcel.write(salida);
			LOG.info("El reporte de compras empleados se creo en... ".concat(archivoXls));
		} catch (IOException e) {
			LOG.error(mensajeError(e));
		}
	}
	
	/**
	 * Metodo para la generacion del archivo con las compras por monto
	 * @param lstComprasCliente
	 */
	@SuppressWarnings("deprecation")
	private static void generarReporteComprasMontoCliente(List<InformacionCompraCliente> lstComprasCliente)
	{

		Calendar sDateCalendar = new GregorianCalendar();

		String semana=String.valueOf(sDateCalendar.get(Calendar.WEEK_OF_YEAR)-1);
		String archivoXls=RUTA_ARCHIVOS_GENERADOS.concat(ARCHIVO_COMPRA_CLIENTES).concat(semana).concat(ARCHIVO_EXCEL_EXTENSION);
		int filaIndice=0;
		File archivo = new File(archivoXls);
		HSSFWorkbook libroExcel = new HSSFWorkbook();
		HSSFSheet hoja1 = libroExcel.createSheet("Compras Clientes Semana ".concat(semana));
		HSSFRow fila = hoja1.createRow(filaIndice);
		HSSFCell celda = fila.createCell((short) 0);
		celda.setCellValue("Nombre empleado");
		HSSFCell celda2 = fila.createCell((short) 1);
		celda2.setCellValue("Cliente Alnova");
		HSSFCell celda3 = fila.createCell((short) 2);
		celda3.setCellValue("ICU");
		HSSFCell celda4 = fila.createCell((short) 3);
		celda4.setCellValue("Compras");
		HSSFCell celda5 = fila.createCell((short) 4);
		celda5.setCellValue("Total compras");
		HSSFCell celda6 = fila.createCell((short) 5);
		celda6.setCellValue("Monto Compras Chedraui");
		HSSFCell celda7 = fila.createCell((short) 6);
		celda7.setCellValue("Monto Compras Walmart");
		
		for(InformacionCompraCliente compraCliente: lstComprasCliente) {
			filaIndice++;
			fila=hoja1.createRow(filaIndice);
			
			HSSFCell celdaReferencia = fila.createCell((short) 0);
			celdaReferencia.setCellValue(compraCliente.getNombreCliente());
			
			HSSFCell celdaNoEmpleado = fila.createCell((short) 1);
			celdaNoEmpleado.setCellValue(compraCliente.getClienteAlnova());
			
			HSSFCell celdaICU = fila.createCell((short) 2);
			celdaICU.setCellValue(compraCliente.getIcu());
			
			StringBuilder informacion=new StringBuilder();
			for(InformacionCompra infoCompra:compraCliente.getLstCompras()) {
				informacion.append(infoCompra.getComproEn()).append(DELIMITADOR_PIPE_EXCEL).append(infoCompra.getReferencia()).append(DELIMITADOR_PIPE_EXCEL).append(infoCompra.getFechaCompra()).append(DELIMITADOR_PIPE_EXCEL).append(infoCompra.getMonto()).append(DELIMITADOR_SALTO_LINEA);
			}
			
			HSSFCell celdaCompras = fila.createCell((short) 3);
			celdaCompras.setCellValue(informacion.toString());
			
			HSSFCell celdaTotalCompras = fila.createCell((short) 4);
			celdaTotalCompras.setCellValue(compraCliente.getNumeroCompras());
			
			HSSFCell celdaMontoComprasChedraui = fila.createCell((short) 5);
			celdaMontoComprasChedraui.setCellValue(compraCliente.getMontoComprasChedraui());
			
			HSSFCell celdaMontoComprasWalmart = fila.createCell((short) 6);
			celdaMontoComprasWalmart.setCellValue(compraCliente.getMontoComprasWalmart());
		}
		try {
			FileOutputStream salida = new FileOutputStream(archivo);
			libroExcel.write(salida);
			LOG.info("El reporte de compras clientes se creo en... ".concat(archivoXls));
		} catch (IOException e) {
			LOG.error(mensajeError(e));
		}
	
	}
	
	/**
	 * Metodo para generar el reporte con informacion obtenida de los archivos de conciliacion
	 * @param lstCompras
	 */
	@SuppressWarnings("deprecation")
	private static void generarReportesCompras(List<InformacionCompraConciliacion> lstCompras)
	{
		String archivoXls=RUTA_ARCHIVOS_GENERADOS.concat(ARCHIVO_REPORTE_COMPRAS);
		int filaIndice=0;
		
		
		LOG.info("Generando reporte de compras con archivos de conciliacion...");
		File archivo = new File(archivoXls);
		HSSFWorkbook libroExcel = new HSSFWorkbook();
		HSSFSheet hoja1 = libroExcel.createSheet(HOJA_REPORTE_COMPRAS);
		HSSFRow fila = hoja1.createRow(filaIndice);
		HSSFCell celda = fila.createCell((short) 0);
		celda.setCellValue(COLUMNA_NEGOCIO);
		HSSFCell celda2 = fila.createCell((short) 1);
		celda2.setCellValue(COLUMNA_MONTO);
		HSSFCell celda3 = fila.createCell((short) 2);
		celda3.setCellValue(COLUMNA_FECHA);
		HSSFCell celda4 = fila.createCell((short) 3);
		celda4.setCellValue(COLUMNA_HORA);
		
		for(InformacionCompraConciliacion compraConciliacion:lstCompras) {
			if(compraConciliacion.getEstatusCompra()!=ESTATUS_REVERSO) {
				filaIndice++;
				fila=hoja1.createRow(filaIndice);
				
				HSSFCell celdaReferencia = fila.createCell((short) 0);
				celdaReferencia.setCellValue(compraConciliacion.getDescripcionAgenteCompra());
				
				HSSFCell celdaNoEmpleado = fila.createCell((short) 1);
				celdaNoEmpleado.setCellValue(compraConciliacion.getMonto());
				
				String fechaHora[]=compraConciliacion.getFechaHoraCompra().split(DELIMITADOR_ESPACIO);
				
				HSSFCell celdaICU = fila.createCell((short) 2);
				celdaICU.setCellValue(fechaHora[0]);
				
				HSSFCell celdaCompras = fila.createCell((short) 3);
				celdaCompras.setCellValue(fechaHora[1]);
			}
		}
		try {
			FileOutputStream salida = new FileOutputStream(archivo);
			libroExcel.write(salida);
			LOG.info("El archivo se creo en... ".concat(archivoXls));
		} catch (IOException e) {
			LOG.error(mensajeError(e));
		}
	}
	
	/**
	 * Metodo para validar que la informacion sea informada
	 * @param informacion
	 * @return boolean
	 */
	private static boolean esInformado(String informacion)
	{
		if(informacion!= null && informacion.trim()!=DELIMITADOR_CADENA_VACIA)
			return true;
		
		return false;
	}
	
	/**
	 * Obtiene la traza de error de la excepcion
	 * @param e
	 * @return String
	 */
	private static String mensajeError(Throwable e)
	{	
	    if(e == null) {
	      return "No hay informacion en la excepcion";
		}
	    StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append("\t at " + element.toString());
			sb.append("\n");
		}
		Throwable exTemp = e;
		while(exTemp.getCause() != null) {
			exTemp = exTemp.getCause();
			sb.append(" Caused by " );
			sb.append(exTemp.toString());
			sb.append("\n");
		}
		return e.toString()  + "\n" + sb.toString();
	}
	
	private static void generarArchivoReporteriaAlnova(List<InformacionCompraConciliacion> lstCompras)
	{
		StringBuilder informacion=new StringBuilder();
		
		LOG.info("Generando archivo para reporteria alnova...");
		lstCompras.forEach(compra -> {
			informacion.append(compra.getIdAgente().concat(agregarEspacios(compra.getReferencia(), 30))).append(DELIMITADOR_SALTO_LINEA);
		});
		SimpleDateFormat formatoFecha=new SimpleDateFormat("yyMMdd");
		Date fechaActual=new Date();
		String fechaArchivo=formatoFecha.format(fechaActual);
		String archivo=RUTA_ARCHIVOS_GENERADOS.concat(ARCHIVO_REPORTERIA_ALNOVA.concat(fechaArchivo));
		File archivoSFTPAlnova=new File(archivo);
		try {
			BufferedWriter buffered=new BufferedWriter(new FileWriter(archivoSFTPAlnova));
			buffered.write(informacion.substring(0));
			buffered.close();
			LOG.info("El archivo se creo en... ".concat(archivo));
		} catch (IOException e) {
			LOG.error(mensajeError(e));
		}
	}
	
	private static String agregarEspacios(String referencia, int posiciones)
	{
		if(referencia.trim().length()==posiciones)
			return referencia;
		StringBuilder nuevaReferencia=new StringBuilder(referencia.trim());
		int espaciosFaltantes=posiciones-referencia.length();
		for(int i=1; i<=espaciosFaltantes; i++) {
			nuevaReferencia.append(DELIMITADOR_ESPACIO);
		}
		
		return nuevaReferencia.substring(0);
	}
}



///**
//* Metodo para leer el archivo de conciliacion sin el ICU del cliente
//* @return  List<InformacionCompraConciliacionSinICU>
//*/
//private static List<InformacionCompraConciliacionSinICU> leerArchivoConciliacionSINICU()
//{
//	List<InformacionCompraConciliacionSinICU> lstArchivoComprasConciliacion=new ArrayList<>();
//	
//	try {
//		Scanner myReader = new Scanner(new File(RUTA_ARCHIVOS.concat(ARCHIVO_COMPRA_COMERCIOS_SIN_ICU)));
//		while (myReader.hasNextLine()) {
//			String[] informacionLinea = myReader.nextLine().split(DELIMITADOR);
//			InformacionCompraConciliacionSinICU informacionCompraConciliacionSinICU=new InformacionCompraConciliacionSinICU();
//			informacionCompraConciliacionSinICU.setIdAgente(informacionLinea[0]);
//			informacionCompraConciliacionSinICU.setDescripcionAgenteCompra(informacionLinea[1]);
//			informacionCompraConciliacionSinICU.setIdCanalCompra(informacionLinea[2]);
//			informacionCompraConciliacionSinICU.setDescripcionCanalCompra(informacionLinea[3]);
//			informacionCompraConciliacionSinICU.setReferencia(informacionLinea[4]);
//			informacionCompraConciliacionSinICU.setIdSusidiaria(informacionLinea[5]);
//			informacionCompraConciliacionSinICU.setDescripcionSubsidiaria(informacionLinea[6]);
//			informacionCompraConciliacionSinICU.setFechaHoraCompra(informacionLinea[7]);
//			informacionCompraConciliacionSinICU.setNumeroAutorizacion(informacionLinea[8]);
//			informacionCompraConciliacionSinICU.setMonto(informacionLinea[9]);
//			informacionCompraConciliacionSinICU.setComisionCliente(informacionLinea[10]);
//			informacionCompraConciliacionSinICU.setIva(informacionLinea[11]);
//			informacionCompraConciliacionSinICU.setComisionAgente(informacionLinea[12]);
//			informacionCompraConciliacionSinICU.setIvaComisionAgente(informacionLinea[13]);
//			informacionCompraConciliacionSinICU.setEstatusCompra(informacionLinea[14]);
//			informacionCompraConciliacionSinICU.setIdTienda(informacionLinea[15]);
//			informacionCompraConciliacionSinICU.setIdCajero(informacionLinea[16]);
//			informacionCompraConciliacionSinICU.setIdCaja(informacionLinea[17]);
//			lstArchivoComprasConciliacion.add(informacionCompraConciliacionSinICU);
//	    }
//	} catch (FileNotFoundException e) {
//		e.printStackTrace();
//	}
//	
//	return lstArchivoComprasConciliacion;
//}
