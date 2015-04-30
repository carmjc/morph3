package net.carmgate.morph.script.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jdk.nashorn.internal.ir.Block;
import jdk.nashorn.internal.ir.FunctionNode;
import jdk.nashorn.internal.ir.Statement;
import jdk.nashorn.internal.parser.Parser;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.ErrorManager;
import jdk.nashorn.internal.runtime.Source;
import jdk.nashorn.internal.runtime.options.Options;

public class NashornASTBuilder {
   public List<Statement> getNashornAst(String resourcePath) throws IOException {
      Options options = new Options("nashorn");
      options.set("anon.functions", true);
      options.set("parse.only", true);
      options.set("scripting", true);
      ErrorManager errors = new ErrorManager();
      Context context = new Context(options, errors, Thread.currentThread().getContextClassLoader());
      Source source = Source.sourceFor("", new File(getClass().getResource(resourcePath).getPath()));
      Parser parser = new Parser(context.getEnv(), source, errors);
      FunctionNode functionNode = parser.parse();
      Block block = functionNode.getBody();
      List<Statement> statements = block.getStatements();
      return statements;
   }

}
