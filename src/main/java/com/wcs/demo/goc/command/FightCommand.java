/** * FightCommand.java 
* Created on 2014年5月8日 下午1:56:08 
*/

package com.wcs.demo.goc.command;

/** 
 * <p>Project: btcbase</p> 
 * <p>Title: FightCommand.java</p> 
 * <p>Description: </p> 
 * <p>Copyright (c) 2014 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yuanzhencai@wcs-global.com">Yuan</a>
 */

public class FightCommand implements Command {
	
	private Receiver receiver;
	
	public FightCommand(Receiver receiver) {
		System.out.println("FightCommand.FightCommand()");
		this.receiver = receiver;
	}
	
	@Override
	public void exe() {
		receiver.fight();
	}

}
