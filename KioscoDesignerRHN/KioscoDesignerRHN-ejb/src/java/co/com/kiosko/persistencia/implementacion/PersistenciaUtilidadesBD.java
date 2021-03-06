package co.com.kiosko.persistencia.implementacion;

import co.com.kiosko.persistencia.interfaz.IPersistenciaUtilidadesBD;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author Felipe Trivi�o
 */
@Stateless
public class PersistenciaUtilidadesBD implements IPersistenciaUtilidadesBD {

    @Override
    public byte[] encriptar(EntityManager eManager, String valor) {
        try {
            String sqlQuery = "SELECT GENERALES_PKG.ENCRYPT(?) FROM DUAL";
            Query query = eManager.createNativeQuery(sqlQuery);
            query.setParameter(1, valor);
            return (byte[]) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error PersistenciaUtilidadesBD.encriptar: " + e);
            return null;
        }
    }

    @Override
    public String desencriptar(EntityManager eManager, byte[] valor) {
        try {
            String sqlQuery = "SELECT GENERALES_PKG.DECRYPT(?) FROM DUAL";
            Query query = eManager.createNativeQuery(sqlQuery);
            query.setParameter(1, valor);
            return (String) query.getSingleResult();
        } catch (Exception e) {
            System.out.println("Error PersistenciaUtilidadesBD.desencriptar: " + e);
            return null;
        }
    }
}
