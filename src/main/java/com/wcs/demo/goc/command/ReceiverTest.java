/** * InvokerTest.java 
 * Created on 2014年5月8日 下午2:06:02 
 */

package com.wcs.demo.goc.command;

import org.junit.Test;

public class ReceiverTest {

	@Test
	public void testActionCommand() {
		Receiver receiver = new Receiver();
		Command cmd = new ActionCommand(receiver);
		Invoker invoker = new Invoker(cmd);
		invoker.action();
	}
	
	@Test
	public void testFightCommand() {
		Receiver receiver = new Receiver();
		Command cmd = new FightCommand(receiver);
		Invoker invoker = new Invoker(cmd);
		invoker.action();
	}

}
