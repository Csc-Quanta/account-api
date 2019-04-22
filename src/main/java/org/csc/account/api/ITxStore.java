package org.csc.account.api;

import java.util.concurrent.Future;

import org.csc.bcapi.backend.ODBException;
import org.csc.bcapi.gens.Oentity.OKey;
import org.csc.bcapi.gens.Oentity.OValue;

public interface ITxStore {

	Future<OValue[]> batchPuts(OKey[] arg0, OValue[] arg1) throws ODBException;

	Future<OValue> delete(OKey arg0) throws ODBException;

	Future<OValue> get(OKey arg0) throws ODBException;

	Future<OValue> get(String arg0) throws ODBException;

	Future<OValue> put(OKey arg0, OValue arg1) throws ODBException;

	Future<OValue> put(String arg0, OValue arg1) throws ODBException;

	Future<OValue> putIfNotExist(OKey arg0, OValue arg1) throws ODBException;

	Future<OValue[]> putIfNotExist(OKey[] arg0, OValue[] arg1) throws ODBException;

}