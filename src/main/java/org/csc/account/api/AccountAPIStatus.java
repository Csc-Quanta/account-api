package org.csc.account.api;

import lombok.Getter;
import lombok.Setter;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;
import org.csc.account.gens.Actimpl.PACTModule;

import com.google.protobuf.Message;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import onight.oapi.scala.commons.SessionModules;
import onight.osgi.annotation.NActorProvider;
import onight.tfw.ntrans.api.ActorService;

@NActorProvider
@Provides(specifications = { ActorService.class })
@Slf4j
@Getter
@Setter
public class AccountAPIStatus extends SessionModules<Message> {

	@Override
	public String[] getCmds() {
		return new String[] { "API" };
	}

	@Override
	public String getModule() {
		return PACTModule.ACT.name();
	}

	@Validate
	public void startup() {

	}

}
