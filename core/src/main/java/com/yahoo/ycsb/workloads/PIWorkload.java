package com.yahoo.ycsb.workloads;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.yahoo.ycsb.ByteIterator;
import com.yahoo.ycsb.Client;
import com.yahoo.ycsb.DB;
import com.yahoo.ycsb.RandomByteIterator;
import com.yahoo.ycsb.Status;
import com.yahoo.ycsb.StringByteIterator;
import com.yahoo.ycsb.WorkloadException;
import com.yahoo.ycsb.generator.AcknowledgedCounterGenerator;
import com.yahoo.ycsb.generator.CounterGenerator;
import com.yahoo.ycsb.generator.ExponentialGenerator;
import com.yahoo.ycsb.generator.HotspotIntegerGenerator;
import com.yahoo.ycsb.generator.ScrambledZipfianGenerator;
import com.yahoo.ycsb.generator.SequentialGenerator;
import com.yahoo.ycsb.generator.SkewedLatestGenerator;
import com.yahoo.ycsb.generator.UniformIntegerGenerator;
import com.yahoo.ycsb.generator.ZipfianGenerator;
import com.yahoo.ycsb.workloads.model.PasswordHash;

public class PIWorkload extends CoreWorkload {
  
	private List<String> fieldnames;

	@Override
	public void init(Properties p) throws WorkloadException {
	    table = p.getProperty(TABLENAME_PROPERTY, TABLENAME_PROPERTY_DEFAULT);

	    fieldcount =
	        Integer.parseInt(p.getProperty(FIELD_COUNT_PROPERTY, FIELD_COUNT_PROPERTY_DEFAULT));
	    
	    fieldnames = new ArrayList<String>();

        fieldnames.add("userName");
        fieldnames.add("password");
        fieldnames.add("userId");
        fieldnames.add("temporaryPassword");
        fieldnames.add("createdDate");
        fieldnames.add("lastUpdatedDate");

    
	    recordcount =
	        Integer.parseInt(p.getProperty(Client.RECORD_COUNT_PROPERTY, Client.DEFAULT_RECORD_COUNT));

	    if (recordcount == 0) {
	      recordcount = Integer.MAX_VALUE;
	    }
	    String requestdistrib =
	        p.getProperty(REQUEST_DISTRIBUTION_PROPERTY, REQUEST_DISTRIBUTION_PROPERTY_DEFAULT);
	    int maxscanlength =
	        Integer.parseInt(p.getProperty(MAX_SCAN_LENGTH_PROPERTY, MAX_SCAN_LENGTH_PROPERTY_DEFAULT));
	    String scanlengthdistrib =
	        p.getProperty(SCAN_LENGTH_DISTRIBUTION_PROPERTY, SCAN_LENGTH_DISTRIBUTION_PROPERTY_DEFAULT);

	    int insertstart =
	        Integer.parseInt(p.getProperty(INSERT_START_PROPERTY, INSERT_START_PROPERTY_DEFAULT));
	    int insertcount =
	        Integer.parseInt(p.getProperty(INSERT_COUNT_PROPERTY, String.valueOf(recordcount - insertstart)));
	    // Confirm valid values for insertstart and insertcount in relation to recordcount
	    if (recordcount < (insertstart + insertcount)) {
	      System.err.println("Invalid combination of insertstart, insertcount and recordcount.");
	      System.err.println("recordcount must be bigger than insertstart + insertcount.");
	      System.exit(-1);
	    }

	    if (p.getProperty(INSERT_ORDER_PROPERTY, INSERT_ORDER_PROPERTY_DEFAULT).compareTo("hashed") == 0) {
	      orderedinserts = false;
	    } else if (requestdistrib.compareTo("exponential") == 0) {
	      double percentile = Double.parseDouble(p.getProperty(
	          ExponentialGenerator.EXPONENTIAL_PERCENTILE_PROPERTY,
	          ExponentialGenerator.EXPONENTIAL_PERCENTILE_DEFAULT));
	      double frac = Double.parseDouble(p.getProperty(
	          ExponentialGenerator.EXPONENTIAL_FRAC_PROPERTY,
	          ExponentialGenerator.EXPONENTIAL_FRAC_DEFAULT));
	      keychooser = new ExponentialGenerator(percentile, recordcount * frac);
	    } else {
	      orderedinserts = true;
	    }

	    keysequence = new CounterGenerator(insertstart);
	    operationchooser = createOperationGenerator(p);

	    transactioninsertkeysequence = new AcknowledgedCounterGenerator(recordcount);
	    if (requestdistrib.compareTo("uniform") == 0) {
	      keychooser = new UniformIntegerGenerator(insertstart, insertstart + insertcount - 1);
	    } else if (requestdistrib.compareTo("sequential") == 0) {
	      keychooser = new SequentialGenerator(insertstart, insertstart + insertcount - 1);
	    }else if (requestdistrib.compareTo("zipfian") == 0) {
	      // it does this by generating a random "next key" in part by taking the modulus over the
	      // number of keys.
	      // If the number of keys changes, this would shift the modulus, and we don't want that to
	      // change which keys are popular so we'll actually construct the scrambled zipfian generator
	      // with a keyspace that is larger than exists at the beginning of the test. that is, we'll predict
	      // the number of inserts, and tell the scrambled zipfian generator the number of existing keys
	      // plus the number of predicted keys as the total keyspace. then, if the generator picks a key
	      // that hasn't been inserted yet, will just ignore it and pick another key. this way, the size of
	      // the keyspace doesn't change from the perspective of the scrambled zipfian generator
	      final double insertproportion = Double.parseDouble(
	          p.getProperty(INSERT_PROPORTION_PROPERTY, INSERT_PROPORTION_PROPERTY_DEFAULT));
	      int opcount = Integer.parseInt(p.getProperty(Client.OPERATION_COUNT_PROPERTY));
	      int expectednewkeys = (int) ((opcount) * insertproportion * 2.0); // 2 is fudge factor

	      keychooser = new ScrambledZipfianGenerator(insertstart, insertstart + insertcount + expectednewkeys);
	    } else if (requestdistrib.compareTo("latest") == 0) {
	      keychooser = new SkewedLatestGenerator(transactioninsertkeysequence);
	    } else if (requestdistrib.equals("hotspot")) {
	      double hotsetfraction =
	          Double.parseDouble(p.getProperty(HOTSPOT_DATA_FRACTION, HOTSPOT_DATA_FRACTION_DEFAULT));
	      double hotopnfraction =
	          Double.parseDouble(p.getProperty(HOTSPOT_OPN_FRACTION, HOTSPOT_OPN_FRACTION_DEFAULT));
	      keychooser = new HotspotIntegerGenerator(insertstart, insertstart + insertcount - 1,
	          hotsetfraction, hotopnfraction);
	    } else {
	      throw new WorkloadException("Unknown request distribution \"" + requestdistrib + "\"");
	    }

	    fieldchooser = new UniformIntegerGenerator(0, fieldcount - 1);

	    if (scanlengthdistrib.compareTo("uniform") == 0) {
	      scanlength = new UniformIntegerGenerator(1, maxscanlength);
	    } else if (scanlengthdistrib.compareTo("zipfian") == 0) {
	      scanlength = new ZipfianGenerator(1, maxscanlength);
	    } else {
	      throw new WorkloadException(
	          "Distribution \"" + scanlengthdistrib + "\" not allowed for scan length");
	    }

	    insertionRetryLimit = Integer.parseInt(p.getProperty(
	        INSERTION_RETRY_LIMIT, INSERTION_RETRY_LIMIT_DEFAULT));
	    insertionRetryInterval = Integer.parseInt(p.getProperty(
	        INSERTION_RETRY_INTERVAL, INSERTION_RETRY_INTERVAL_DEFAULT));	
	}

	@Override
	public String buildKeyName(long keynum) {
		// TODO Auto-generated method stub
		return super.buildKeyName(keynum);
	}
	/**
	* Builds values for all fields.
	*/
	private HashMap<String, ByteIterator> buildValues(String key) {
		HashMap<String, ByteIterator> values = new HashMap<String, ByteIterator>();

	    for (String fieldkey : fieldnames) {
	      ByteIterator data;
	      if (dataintegrity) {
	        data = new StringByteIterator(buildDeterministicValue(key, fieldkey));
	      } else {
	        // fill with random data
	        data = new RandomByteIterator(fieldlengthgenerator.nextValue().longValue());
	      }
	      values.put(fieldkey, data);
	    }
	    return values;
	}
	
	
	@Override
	public boolean doInsert(DB db, Object threadstate) {
		
	    int keynum = keysequence.nextValue().intValue();
	    
	    String dbkey = buildKeyName(keynum);
	    
	    HashMap<String, ByteIterator> values = buildValues(dbkey);

	    Status status;
	    int numOfRetries = 0;
	    do {
	      status = db.insert(table, dbkey, values);
	      if (null != status && status.isOk()) {
	        break;
	      }
	      // Retry if configured. Without retrying, the load process will fail
	      // even if one single insertion fails. User can optionally configure
	      // an insertion retry limit (default is 0) to enable retry.
	      if (++numOfRetries <= insertionRetryLimit) {
	        System.err.println("Retrying insertion, retry count: " + numOfRetries);
	        try {
	          // Sleep for a random number between [0.8, 1.2)*insertionRetryInterval.
	          int sleepTime = (int) (1000 * insertionRetryInterval * (0.8 + 0.4 * Math.random()));
	          Thread.sleep(sleepTime);
	        } catch (InterruptedException e) {
	          break;
	        }

	      } else {
	        System.err.println("Error inserting, not retrying any more. number of attempts: " + numOfRetries +
	            "Insertion Retry Limit: " + insertionRetryLimit);
	        break;

	      }
	    } while (true);

	    return null != status && status.isOk();
	}

	@Override
	public boolean doTransaction(DB db, Object threadstate) {
		// TODO Auto-generated method stub
		return super.doTransaction(db, threadstate);
	}

	@Override
	public void doTransactionInsert(DB db) {
		// TODO Auto-generated method stub
		super.doTransactionInsert(db);
	}
	
	//credential.setId(UniqueIdGenerator.generateIncrementing().toString());
}
