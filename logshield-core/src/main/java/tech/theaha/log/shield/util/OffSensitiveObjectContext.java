package tech.theaha.log.shield.util;

import java.util.Map;

import tech.theaha.log.shield.bc.OffSensitiveHandler;

@SuppressWarnings("rawtypes")
public	class OffSensitiveObjectContext {
		private Object object;
		private OffSensitiveHandler handler;
		private Map<String, String> restoreValue;

		public OffSensitiveObjectContext(Object object, OffSensitiveHandler handler, Map<String, String> restoreValue) {
			super();
			this.object = object;
			this.handler = handler;
			this.restoreValue = restoreValue;
		}

		public Object getObject() {
			return object;
		}

		public OffSensitiveHandler getHandler() {
			return handler;
		}

		public Map<String, String> getRestoreValue() {
			return restoreValue;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public void setHandler(OffSensitiveHandler handler) {
			this.handler = handler;
		}

		public void setRestoreValue(Map<String, String> restoreValue) {
			this.restoreValue = restoreValue;
		}
	}