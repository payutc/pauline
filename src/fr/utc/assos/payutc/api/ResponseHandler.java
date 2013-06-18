package fr.utc.assos.payutc.api;

public interface ResponseHandler<T> {
	public void onSuccess(T response);
	public void onError(Exception ex);
}
