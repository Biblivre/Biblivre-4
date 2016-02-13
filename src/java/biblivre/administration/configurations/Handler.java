/*******************************************************************************
 * Este arquivo é parte do Biblivre4.
 * 
 * Biblivre4 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.administration.configurations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;

public class Handler extends AbstractHandler {

	@SuppressWarnings("unchecked")
	public void save(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		int loggedUser = request.getLoggedUserId();

		String configurations = request.getString("configurations", "{}");
		List<ConfigurationsDTO> configs = new ArrayList<ConfigurationsDTO>();

		try {
			JSONObject json = new JSONObject(configurations);

			Iterator<String> it = json.keys();
			while (it.hasNext()) {
				String key = it.next();
				String value = json.getString(key);

				configs.add(new ConfigurationsDTO(key, value));
			}
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
			return;
		}

		if (configs.size() == 0) {
			return;
		}
		
		try {
			configs = Configurations.validate(schema, configs);
		} catch (ValidationException e) {
			this.setMessage(e);
			return;
		}
		
		try {
			Configurations.save(schema, configs, loggedUser);
			this.setMessage(ActionResult.SUCCESS, "administration.configurations.save.success");
		} catch (Exception e) {
			this.setMessage(ActionResult.WARNING, "administration.configurations.error.save");
			return;
		}
	}
}
