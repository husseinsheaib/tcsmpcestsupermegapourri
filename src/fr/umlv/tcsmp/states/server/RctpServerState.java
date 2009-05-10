package fr.umlv.tcsmp.states.server;

import java.nio.ByteBuffer;
import java.text.ParseException;

import fr.umlv.tcsmp.proto.Protocol;
import fr.umlv.tcsmp.proto.Response;
import fr.umlv.tcsmp.states.TCSMPState;
import fr.umlv.tcsmp.utils.ErrorReplies;
import fr.umlv.tcsmp.utils.TCSMPParser;

public class RctpServerState implements TCSMPState {

	@Override
	public Response processCommand(Protocol proto, ByteBuffer bb) {
		String [] args = TCSMPParser.parse(bb);

		/**
		 * ICI on va s'amuser puisque on ne switch pas forcement de
		 * state.
		 * 
		 * Qu'est ce qu'on fait ?
		 * 
		 * 1 solution:
		 * 
		 * 	- on accept ici la commande APZL.
		 *  - si on lit un APZL, on passe le state en APZL
		 *  - on appelle processCommand du state APZL avec le bb qu'on a lu
		 *  
		 */
		
		if (args.length > 2 || (args[0].equals("RCPT") == false && args[0].equals("APZL") == false)) {
			return new Response(ErrorReplies.unknowCommand("RCTP|APZL", args[0]));
		}
		
		if (args[0].equals("APZL")) {
			/* bypass normal proccessings */
			TCSMPState apzlState = new ApzlServerState();
			proto.setState(apzlState);
			return apzlState.processCommand(proto, bb);
		}
		
		
		String dest = null;
		try {
			dest = TCSMPParser.parseDomain(args[1]);
		} catch (ParseException e) {
			ByteBuffer response = ByteBuffer.wrap("501 Not a valid address.".getBytes());
			return new Response(response);
		}
		
		/**
		 * XXX: Here we have to see if we have to forward
		 * command or if we catch it. 
		 */
		
		if (dest.equals("mydomain")) {
			/** XXX: see if user exists here ? */
			ByteBuffer response = ByteBuffer.wrap("250 OK".getBytes());
			return new Response(response);
		}
		
		/* bb will be forwarded to the dest domain */
		return new Response(bb, dest);
	}
}
