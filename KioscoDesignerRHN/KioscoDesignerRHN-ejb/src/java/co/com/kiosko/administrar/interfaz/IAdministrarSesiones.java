package co.com.kiosko.administrar.interfaz;

import co.com.kiosko.clasesAyuda.SessionEntityManager;
import javax.persistence.EntityManager;

/**
 *
 * @author Administrador
 */
public interface IAdministrarSesiones {

    public void adicionarSesion(SessionEntityManager session);

    public void consultarSessionesActivas();

    public EntityManager obtenerConexionSesion(String idSesion);

    public void borrarSesion(String idSesion);
}
