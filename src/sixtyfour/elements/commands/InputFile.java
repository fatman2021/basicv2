package sixtyfour.elements.commands;

import java.util.List;

import sixtyfour.elements.Type;
import sixtyfour.elements.Variable;
import sixtyfour.parser.Atom;
import sixtyfour.parser.Parser;
import sixtyfour.parser.Term;
import sixtyfour.plugins.DeviceProvider;
import sixtyfour.system.Machine;
import sixtyfour.system.ProgramCounter;

public class InputFile extends Input {
	private Atom fileNumber = null;

	public InputFile() {
		super("INPUT#");
	}

	@Override
	public String parse(String linePart, int lineCnt, int lineNumber, int linePos, boolean lastPos, Machine machine) {
		linePart = linePart.substring(this.name.length());
		int pos = linePart.indexOf(',');
		if (pos == -1) {
			pos = linePart.length();
		}
		term = Parser.getTerm(linePart.substring(0, pos), machine, false, true);
		linePart = pos != linePart.length() ? linePart.substring(pos + 1) : "";
		List<Atom> pars = Parser.getParameters(term);
		if (pars.size() != 1) {
			throw new RuntimeException("Syntax error: " + this);
		}
		fileNumber = pars.get(0);
		if (fileNumber.getType().equals(Type.STRING)) {
			throw new RuntimeException("Type mismatch error: " + this);
		}
		super.parse("INPUT" + linePart, lineCnt, lineNumber, linePos, lastPos, machine);
		if (comment != null && !comment.isEmpty()) {
			throw new RuntimeException("Syntax error: " + this);
		}
		return null;
	}

	@Override
	public ProgramCounter execute(Machine machine) {
		int fn = ((Number) fileNumber.eval(machine)).intValue();
		DeviceProvider device = machine.getDeviceProvider();
		for (int i = 0; i < vars.size(); i++) {
			Term indexTerm = indexTerms.get(i);
			Variable var = this.getVariable(machine, i);
			Type varType = var.getType();

			if (indexTerm != null) {
				// array
				List<Atom> pars = Parser.getParameters(indexTerm);
				int[] pis = new int[pars.size()];
				int cnt = 0;
				for (Atom par : pars) {
					pis[cnt++] = ((Number) par.eval(machine)).intValue();
				}
				try {
					if (varType.equals(Type.STRING)) {
						var.setValue(device.inputString(fn), pis);
					} else if (varType.equals(Type.REAL)) {
						var.setValue(device.inputNumber(fn), pis);
					} else if (varType.equals(Type.INTEGER)) {
						var.setValue(device.inputNumber(fn).intValue(), pis);
					}
				} catch (Exception nfe) {
					throw new RuntimeException("Bad data error: " + this);
				}
			} else {
				// no array
				if (varType.equals(Type.STRING)) {
					var.setValue(device.inputString(fn));
				} else {
					if (varType.equals(Type.INTEGER)) {
						try {
							Integer num = device.inputNumber(fn).intValue();
							var.setValue(num);
						} catch (Exception nfe) {
							throw new RuntimeException("Bad data error: " + this);
						}
					} else {
						try {
							Float num = device.inputNumber(fn);
							var.setValue(num);
						} catch (Exception nfe) {
							throw new RuntimeException("Bad data error: " + this);
						}
					}
				}
			}
		}
		return null;
	}
}