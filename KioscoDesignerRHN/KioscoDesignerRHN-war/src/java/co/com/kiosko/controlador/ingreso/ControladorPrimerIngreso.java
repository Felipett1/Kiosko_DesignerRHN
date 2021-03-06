package co.com.kiosko.controlador.ingreso;

import co.com.kiosko.administrar.entidades.ConexionesKioskos;
import co.com.kiosko.administrar.entidades.ParametrizaClave;
import co.com.kiosko.administrar.entidades.PreguntasKioskos;
import co.com.kiosko.administrar.interfaz.IAdministrarPrimerIngreso;
import co.com.kiosko.utilidadesUI.MensajesUI;
import co.com.kiosko.utilidadesUI.PrimefacesContextUI;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Felipe Trivi�o
 */
@ManagedBean
@ViewScoped
public class ControladorPrimerIngreso implements Serializable {

    @EJB
    private IAdministrarPrimerIngreso administrarPrimerIngreso;
    private List<PreguntasKioskos> lstPreguntasKiosko;
    private ConexionesKioskos nuevoIngreso;
    private String clave, confirmacion;
    //VISIBILIDAD PANELES
    private String cssPanelPreguntas, cssPanelClave;
    //VALORES INGRESO
    private String usuario, nit;
    //FORMATO CLAVE
    private ParametrizaClave pc;

    public ControladorPrimerIngreso() {
        nuevoIngreso = new ConexionesKioskos();
        cssPanelPreguntas = "";
        cssPanelClave = "display: none";
    }

    @PostConstruct
    public void inicializarAdministrador() {
        try {
            FacesContext x = FacesContext.getCurrentInstance();
            HttpSession ses = (HttpSession) x.getExternalContext().getSession(false);
            administrarPrimerIngreso.obtenerConexion(ses.getId());
            usuario = ((ControladorIngreso) x.getApplication().evaluateExpressionGet(x, "#{controladorIngreso}", ControladorIngreso.class)).getUsuario();
            nit = ((ControladorIngreso) x.getApplication().evaluateExpressionGet(x, "#{controladorIngreso}", ControladorIngreso.class)).getNit();
            pc = administrarPrimerIngreso.obtenerFormatoClave(Long.parseLong(nit));
            requerirPreguntasSeguridad();
        } catch (Exception e) {
            System.out.println("Error postconstruct " + this.getClass().getName() + ": " + e);
            System.out.println("Causa: " + e.getCause());
        }
    }

    public void continuar() {
        if (nuevoIngreso.getPregunta1() != null && nuevoIngreso.getPregunta2() != null
                && nuevoIngreso.getRespuesta1UI() != null && nuevoIngreso.getRespuesta2UI() != null
                && !nuevoIngreso.getRespuesta1UI().isEmpty() && !nuevoIngreso.getRespuesta2UI().isEmpty()) {
            if (!nuevoIngreso.getPregunta1().getSecuencia().equals(nuevoIngreso.getPregunta2().getSecuencia())) {
                cssPanelPreguntas = "display: none";
                cssPanelClave = "";
            } else {
                MensajesUI.warn("Por favor seleccione dos preguntas diferentes.");
            }
        } else {
            MensajesUI.warn("Todos los campos son obligatorios.");
        }
    }

    public void atras() {
        cssPanelPreguntas = "";
        cssPanelClave = "display: none";
    }

    public void finalizar() {
        if (clave != null && !clave.isEmpty() && confirmacion != null && !confirmacion.isEmpty()) {
            if (clave.equals(confirmacion)) {
                if (validarClave(clave)) {
                    nuevoIngreso.setEmpleado(administrarPrimerIngreso.consultarEmpleado(new BigInteger(usuario)));
                    nuevoIngreso.setPwd(administrarPrimerIngreso.encriptar(clave));
                    byte[] rsp1, rsp2;
                    nuevoIngreso.setRespuesta1(administrarPrimerIngreso.encriptar(nuevoIngreso.getRespuesta1UI().toUpperCase()));
                    nuevoIngreso.setRespuesta2(administrarPrimerIngreso.encriptar(nuevoIngreso.getRespuesta2UI().toUpperCase()));
                    nuevoIngreso.setActivo("S");
                    nuevoIngreso.setUltimaconexion(new Date());
                    nuevoIngreso.setEnviocorreo("N");
                    if (administrarPrimerIngreso.registrarConexionKiosko(nuevoIngreso)) {
                        PrimefacesContextUI.ejecutar("PF('dlgProcesoFinalizado').show()");
                    } else {
                        MensajesUI.error("Se ha generado un error inesperado, por favor contacte a soporte.");
                    }
                }
            } else {
                MensajesUI.warn("La contrase�a no coincide, por favor verifique.");
            }
        } else {
            MensajesUI.warn("Todos los campos son obligatorios.");
        }
    }

    // (			# Start of group
    // (?=.*\d)                 #   must contains one digit from 0-9
    // (?=.*[a-z])		#   must contains one lowercase characters
    // (?=.*[A-Z])		#   must contains one uppercase characters
    // (?=.*[@#$%])		#   must contains one special symbols in the list "@#$%"
    //            .		#     match anything with previous condition checking
    //             {6,20}	#        length at least 6 characters and maximum of 20	
    // )    			# End of group
    public boolean validarClave(String clave) {
        try {
            String PATTERN_PASSWORD = pc.getFormato();
            Pattern pattern = Pattern.compile(PATTERN_PASSWORD);
            Matcher matcher = pattern.matcher(clave);
            if (matcher.matches()) {
                return true;
            } else {
                MensajesUI.error("Contrase�a inv�lida: " + pc.getMensajevalidacion());
            }
        } catch (Exception e) {
            MensajesUI.error("Error en el formato de clave. Por favor contacte a soporte.");
        }

        return false;
    }

    public void requerirPreguntasSeguridad() {
        lstPreguntasKiosko = administrarPrimerIngreso.obtenerPreguntasSeguridad();
    }

    //GETTER AND SETTER
    public List<PreguntasKioskos> getLstPreguntasKiosko() {
        return lstPreguntasKiosko;
    }

    public void setLstPreguntasKiosko(List<PreguntasKioskos> lstPreguntasKiosko) {
        this.lstPreguntasKiosko = lstPreguntasKiosko;
    }

    public ConexionesKioskos getNuevoIngreso() {
        return nuevoIngreso;
    }

    public void setNuevoIngreso(ConexionesKioskos nuevoIngreso) {
        this.nuevoIngreso = nuevoIngreso;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(String confirmacion) {
        this.confirmacion = confirmacion;
    }

    public String getCssPanelClave() {
        return cssPanelClave;
    }

    public void setCssPanelClave(String cssPanelClave) {
        this.cssPanelClave = cssPanelClave;
    }

    public String getCssPanelPreguntas() {
        return cssPanelPreguntas;
    }

    public void setCssPanelPreguntas(String cssPanelPreguntas) {
        this.cssPanelPreguntas = cssPanelPreguntas;
    }
}
