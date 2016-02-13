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
package biblivre.administration.setup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONException;

import biblivre.administration.backup.BackupBO;
import biblivre.administration.backup.BackupScope;
import biblivre.administration.backup.RestoreBO;
import biblivre.administration.backup.RestoreDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.StaticBO;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.MemoryFile;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;

public class Handler extends AbstractHandler {

	public void cleanInstall(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		ConfigurationsDTO dto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
		Configurations.save(schema, dto, 0);

		try {
			this.json.put("success", true);
		} catch (JSONException e) {}
	}
	
	// http://localhost:8080/Biblivre4/?controller=json&module=administration.backup&action=list_restores
	public void listRestores(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		RestoreBO bo = RestoreBO.getInstance(schema);

		LinkedList<RestoreDTO> list = bo.list();
		
		try {
			this.json.put("success", true);

			for (RestoreDTO dto : list) {
				this.json.append("restores", dto.toJSONObject());
			}
		} catch (JSONException e) {}
	}

	public void uploadBiblivre4(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		BackupBO bo = BackupBO.getInstance(schema);
		MemoryFile file = request.getFile("biblivre4backup");
		File path = bo.getBackupDestination();
		String uuid = UUID.randomUUID().toString();
		File backup = new File(path, uuid);
		
		boolean success = true;
		try {
			file.copy(new FileOutputStream(backup));
		} catch (Exception e) {
			success = false;
		}
		try {
			this.json.put("success", success);
			this.json.put("file", uuid);
		} catch (JSONException e) {}
	}
	
	public void uploadBiblivre3(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		
		boolean success = false;		
		try {
			State.start();
			State.writeLog(request.getLocalizedText("administration.setup.biblivre3restore.log_header"));

			MemoryFile file = request.getFile("biblivre3backup");
			File gzip = new File(FileIOUtils.createTempDir(), file.getName());
			OutputStream os = new FileOutputStream(gzip);
			
			file.copy(os);

			os.close();
			
			RestoreBO bo = RestoreBO.getInstance(schema);
			success = bo.restoreBiblivre3(gzip);
			
			if (success) {
				State.finish();
			} else {
				State.cancel();
			}
		} catch (ValidationException e) {
			this.setMessage(e);
			State.writeLog(request.getLocalizedText(e.getMessage()));
			State.cancel();
		} catch (Exception e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();
		}

		try {
			this.json.put("success", success);
		} catch (JSONException e) {}
	}
	

	// http://localhost:8080/Biblivre4/?controller=json&module=administration.backup&action=restore&filename=Biblivre Backup 2012-09-15 22h56m22s Full.b4bz
	public void restore(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String filename = request.getString("filename");

		boolean success = false;
		try {
			State.start();
			State.writeLog(request.getLocalizedText("administration.setup.biblivre4restore.log_header"));
			
			BackupBO bbo = BackupBO.getInstance(schema);
			BackupScope restoreScope = bbo.getBackupScope();
			
			// TODO: Completar importação de backup
			Map<String, String> restoreSchemas = new HashMap<String, String>();
			restoreSchemas.put("global", "global");
			restoreSchemas.put("single", "single");
			
			RestoreBO bo = RestoreBO.getInstance(schema);
			RestoreDTO dto = bo.getRestoreDTO(filename);
			
			dto.setRestoreScope(restoreScope);
			dto.setRestoreSchemas(restoreSchemas);
			
			success = bo.restore(dto);

			if (success) {
				ConfigurationsDTO cdto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
				Configurations.save(schema, cdto, 0);
				
				StaticBO.resetCache();
				
				State.finish();
			} else {
				State.cancel();
			}
		} catch (ValidationException e) {
			this.setMessage(e);
			State.writeLog(request.getLocalizedText(e.getMessage()));
			State.cancel();
		} catch (Exception e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();
		}
		
		try {
			this.json.put("success", success);
		} catch (JSONException e) {}
	}


	public void importBiblivre3(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();
		String origin = request.getString("origin", "biblivre3");

		String[] groups = request.getParameterValues("groups[]");
		List<DataMigrationPhaseGroup> phaseGroups = new ArrayList<DataMigrationPhaseGroup>();

		if (groups != null) {
			for (String group : groups) {
				phaseGroups.add(DataMigrationPhaseGroup.fromString(group));
			}
		}

		if (phaseGroups.size() == 0) {
			this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
			return;
		}

		List<DataMigrationPhase> selectedPhases = new ArrayList<DataMigrationPhase>();
		for (DataMigrationPhaseGroup group : phaseGroups) {
			selectedPhases.addAll(group.getPhases());
		}
		
		boolean success = false;

		try {
			State.start();
			State.writeLog(request.getLocalizedText("administration.setup.biblivre3import.log_header"));
			
			success = DataMigrationBO.getInstance(schema, origin).migrate(selectedPhases);

			if (success) {
				ConfigurationsDTO cdto = new ConfigurationsDTO(Constants.CONFIG_NEW_LIBRARY, "false");
				Configurations.save(schema, cdto, 0);
				
				StaticBO.resetCache();
				
				State.finish();
			} else {
				State.cancel();
			}
		} catch (ValidationException e) {
			this.setMessage(e);
			State.writeLog(request.getLocalizedText(e.getMessage()));
			State.cancel();
		} catch (Exception e) {
			this.setMessage(e);
			State.writeLog(ExceptionUtils.getStackTrace(e));
			State.cancel();
		}
		
		try {
			this.json.put("success", success);
		} catch (JSONException e) {}		
	}
	
	public void progress(ExtendedRequest request, ExtendedResponse response) {
		try {
			this.json.put("success", true);
			this.json.put("current", State.getCurrentStep());
			this.json.put("total", State.getSteps());
			this.json.put("secondary_current", State.getCurrentSecondaryStep());
			this.json.put("complete", !State.LOCKED.get());
		} catch (JSONException e) {}
	}
}
