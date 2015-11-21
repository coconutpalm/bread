package com.coconut_palm_software.bread.lib.index;

import static com.coconut_palm_software.bread.optionmonad.None.none;
import static com.coconut_palm_software.bread.optionmonad.Nulls.option;

import java.util.List;
import java.util.SortedMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.coconut_palm_software.bread.lib.IBibleReference;
import com.coconut_palm_software.bread.lib.Installation;
import com.coconut_palm_software.bread.optionmonad.F;
import com.coconut_palm_software.bread.optionmonad.Option;

public class NetBibleIndexer {
	private final String indexDirectory = Installation.getBasePath() + "/index";
	private Option<SortedMap<String, List<IBibleReference>>> index = none();
	
	private static NetBibleIndexer singleton = null;
	
	public static NetBibleIndexer getDefault() {
		if (singleton == null) {
			new NetBibleIndexer();
		}
		return singleton;
	}
	
	public NetBibleIndexer() {
		singleton = this;
		if (!index.hasValue()) {
			index = loadIndex();
		}
		if (!index.hasValue()) {
			generateIndexInBackground();
		}
	}
	
	private Option<SortedMap<String, List<IBibleReference>>> loadIndex() {
		SortedMap<String, List<IBibleReference>> result = null;
		return option(result);
	}

	private void generateIndexInBackground() {
		Job backgroundWork = new Job("Indexing NET Bible text") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Option<SortedMap<String, List<IBibleReference>>> result = generateIndex();
				if (result.hasValue()) {
					NetBibleIndexer.this.index = result;
					storeIndex(result.get());
				}
				return Status.OK_STATUS;
			}
			
			private Option<SortedMap<String, List<IBibleReference>>> generateIndex() {
				return none();
			}

			private void storeIndex(SortedMap<String, List<IBibleReference>> sortedMap) {
			}
		};
		backgroundWork.schedule();
	}

}
