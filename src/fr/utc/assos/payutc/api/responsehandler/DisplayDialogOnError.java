package fr.utc.assos.payutc.api.responsehandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import fr.utc.assos.payutc.api.ResponseHandler;



public abstract class DisplayDialogOnError<T> implements ResponseHandler<T> {

	protected Activity ctx;
	protected String dialogTitle;
	protected DialogInterface.OnClickListener againListener = null;
	protected boolean exitOnFailure;

	public DisplayDialogOnError(Activity ctx, String dialogTitle) {
		this(ctx, dialogTitle, null);
	}
	
	public DisplayDialogOnError(Activity ctx, String dialogTitle,
			DialogInterface.OnClickListener againListener) {
		this(ctx, dialogTitle, null, false);
	}
	
	public DisplayDialogOnError(Activity ctx, String dialogTitle,
			DialogInterface.OnClickListener againListener, boolean exitOnFailure) {
		this.ctx = ctx;
		this.dialogTitle = dialogTitle;
		this.againListener = againListener;
		this.exitOnFailure = exitOnFailure;
	}
	
	@Override
	public void onError(Exception ex) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setTitle(dialogTitle)
			.setMessage(computeErrorMessage(ex))
			.setCancelable(false)
			.setNegativeButton(ctx.getString(ctx.getResources().getIdentifier("cancel", "string", ctx.getPackageName())), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                if (exitOnFailure) {
		                	ctx.finish();
		                }
		           }});
		if (againListener != null) {
			builder.setPositiveButton(ctx.getString(ctx.getResources().getIdentifier("retry", "string", ctx.getPackageName())), againListener);
		}
		builder.create().show();
	}
	
	protected String computeErrorMessage(Exception ex) {
		return ex.getMessage();
	}
}
