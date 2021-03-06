package com.sixtyfour.cbmnative.javascript.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.sixtyfour.cbmnative.Generator;

public class GeneratorListJs {

	private static final Generator LINE_NUMBER_GENERATOR = new LineNumberGeneratorJs();

	private final static List<Generator> GENERATORS = Collections.unmodifiableList(new ArrayList<Generator>() {
		private static final long serialVersionUID = 1L;
		{
			this.add(new NopJs());
			this.add(new RtsJs());
			this.add(new JsrJs());
			this.add(new JmpJs());
			this.add(new JneJs());
			this.add(new JeJs());
			this.add(new MovJs());
			this.add(new MovbJs());
			this.add(new AddJs());
			this.add(new SubJs());
			this.add(new MulJs());
			this.add(new DivJs());
			this.add(new PopJs());
			this.add(new PushJs());
			this.add(new ShrJs());
			this.add(new ShlJs());
			this.add(new EqJs());
			this.add(new LtJs());
			this.add(new GtJs());
			this.add(new NeqJs());
			this.add(new GteqJs());
			this.add(new LteqJs());
			this.add(new CmpJs());
			this.add(new AndJs());
			this.add(new IntJs());
			this.add(new AbsJs());
			this.add(new AtnJs());
			this.add(new CosJs());
			this.add(new SinJs());
			this.add(new TanJs());
			this.add(new RndJs());
			this.add(new SqrJs());
			this.add(new PowJs());
			this.add(new SgnJs());
			this.add(new BrkJs());
			this.add(new OrJs());
			this.add(new LogJs());
			this.add(new ExpJs());
			this.add(new XorJs());
		}
	});

	private final static Map<String, Generator> NAME_2_GEN = new HashMap<String, Generator>() {
		private static final long serialVersionUID = 1L;
		{
			for (Generator gen : GENERATORS) {
				this.put(gen.getMnemonic(), gen);
			}
		}
	};

	public static Generator getGenerator(String line) {
		line = line.trim().split(" ")[0].toUpperCase(Locale.ENGLISH);
		if (line.endsWith(":")) {
			return LINE_NUMBER_GENERATOR;
		}
		return NAME_2_GEN.get(line);
	}
}
