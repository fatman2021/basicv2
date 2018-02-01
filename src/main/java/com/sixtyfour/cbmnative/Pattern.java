package com.sixtyfour.cbmnative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author EgonOlsen
 * 
 */
public class Pattern {
	private List<String> pattern;
	private int pos = 0;
	private String[] regs = new String[10];
	private String[] mems = new String[10];
	private String[] replacement;
	private int index = -1;
	private int end = -1;
	private String name;

	public Pattern(String name, String[] replacement, String... parts) {
		pattern = new ArrayList<>(Arrays.asList(parts));
		this.replacement = replacement;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<String> apply(List<String> code) {
		if (pos == pattern.size()) {
			List<String> first = code.subList(0, index);
			List<String> last = code.subList(end + 1, code.size());
			String[] replacement = null;
			if (this.replacement != null) {
				replacement = Arrays.copyOf(this.replacement, this.replacement.length);
				for (int i = 0; i < replacement.length; i++) {
					if (replacement[i].startsWith("{LINE")) {
						int num = Integer.parseInt(replacement[i].substring(5, replacement[i].length() - 1));
						List<String> sub = new ArrayList<String>(code.subList(index, end + 1));
						for (Iterator<String> sitty = sub.iterator(); sitty.hasNext();) {
							if (sitty.next().startsWith(";")) {
								sitty.remove();
							}
						}
						replacement[i] = sub.get(num);
					} else {
						int pos = replacement[i].indexOf("{REG");
						if (pos != -1) {
							int posi = Integer.valueOf(replacement[i].substring(pos + 4, replacement[i].length() - 1));
							replacement[i] = replacement[i].substring(0, pos) + " " + regs[posi];
						}
					}
				}
			}
			List<String> eternity = replacement != null ? new ArrayList<String>(Arrays.asList(replacement)) : new ArrayList<String>();
			eternity.add("; Optimizer rule: " + name + "/" + (replacement == null ? 0 : replacement.length));
			List<String> res = new ArrayList<>(first);
			res.addAll(eternity);
			res.addAll(last);
			resetPattern();
			return res;
		}
		resetPattern();
		return code;
	}

	public boolean matches(String line, int ix, Map<String, Number> const2Value) {
		String part = pattern.get(pos);
		line = line.trim();
		if (line.startsWith(";")) {
			return false;
		}
		int p0 = part.indexOf(" ");
		int p1 = line.indexOf(" ");
		if (p0 == -1 && p1 == -1 && part.equalsIgnoreCase(line)) {
			return inc(ix);
		}
		if (part.equals("{LABEL}") && line.endsWith(":")) {
			return inc(ix);
		}
		if (p0 != -1 && p1 != -1 && !line.contains("SKIP")) {
			// System.out.println("Checking: " + line + " / " + part+"/"+pos+"/"+pattern.size());
			if (part.substring(0, p0).equalsIgnoreCase(line.substring(0, p1))) {
				String partRight = part.substring(p0 + 1).trim();
				String lineRight = line.substring(p1 + 1).trim();
				if (partRight.equalsIgnoreCase(lineRight)) {
					return inc(ix);
				} else {
					p0 = partRight.indexOf("{");
					if (p0 != -1) {
						String leftPart = partRight.substring(0, p0);
						if (lineRight.startsWith(leftPart)) {
							String value = lineRight.substring(p0);
							p1 = partRight.lastIndexOf("}");
							String reg = partRight.substring(p0 + 1, p1);
							if (reg.equals("*")) {
								return inc(ix);
							} else {
								if (reg.startsWith("#")) {
									String num = reg.replace("#", "");
									boolean isReal = num.contains(".");
									Number val = Float.valueOf(num);
									if (!isReal) {
										val = val.intValue();
									}
									int pos = lineRight.indexOf("CONST_");
									if (lineRight.equals(reg)
											|| (pos != -1 && const2Value.containsKey(lineRight.substring(pos)) && const2Value.get(lineRight.substring(pos)).floatValue()==val.floatValue())) {
										return inc(ix);
									}
									return resetPattern();
								} else {
									if (lineRight.endsWith("_REG") && reg.startsWith("REG")) {
										int num = Integer.parseInt(reg.replace("REG", ""));
										if (regs[num] == null) {
											regs[num] = value;
											return inc(ix);
										} else {
											if (regs[num].equalsIgnoreCase(value)) {
												return inc(ix);
											} else {
												return resetPattern();
											}
										}
									} else {
										if ((lineRight.contains("VAR_") || lineRight.contains("CONST_") || isNumber(lineRight)) && reg.startsWith("MEM")) {
											int pv = value.lastIndexOf("+");
											if (pv != -1) {
												value = value.substring(0, pv);
											}
											int num = Integer.parseInt(reg.replace("MEM", ""));
											if (mems[num] == null) {
												mems[num] = value;
												return inc(ix);
											} else {
												if (mems[num].equalsIgnoreCase(value)) {
													return inc(ix);
												} else {
													return resetPattern();
												}
											}
										} else {
											return resetPattern();
										}
									}
								}
							}
						} else {
							return resetPattern();
						}
					} else {
						return resetPattern();
					}
				}
			}
		}
		return resetPattern();
	}

	private boolean isNumber(String lineRight) {
		try {
			Integer.parseInt(lineRight);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean resetPattern() {
		pos = 0;
		index = -1;
		end = -1;
		regs = new String[regs.length];
		mems = new String[mems.length];
		return false;
	}

	private boolean inc(int ix) {
		pos++;
		if (index == -1) {
			index = ix;
		}
		end = ix;
		return pos == pattern.size();
	}

}