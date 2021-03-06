/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.migrationanalyzer.commandline;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

abstract class AbstractMigrationAnalysis {

    private static final String DESCRIPTION = "  Produces a migration analysis report for each archive found at the specified input path. "
        + "The input path may be either a single archive or a directory. In the case of a directory, the entire directory "
        + "structure is examined and all archives that are found are analyzed. The reports are written to the output path with each "
        + "report being written into a separate sub-directory. The sub-directory's name is of the form <archive-name>.migration-analysis. "
        + "For example, if my-app.ear is analyzed its report will be written to <outputPath>/my-app.ear.migration-analysis.";

    private static final int OPTIONS_WIDTH = 80;

    private static final int OPTIONS_INDENT = 2;

    private static final Options OPTIONS = new OptionsFactory().create();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    final void run(String[] args) {
        CommandLine commandLine = null;
        try {
            commandLine = new PosixParser().parse(OPTIONS, args);
            String[] outputTypes = commandLine.getOptionValues(OptionsFactory.OPTION_KEY_OUTPUT_TYPE);
            String outputPath = commandLine.getOptionValue(OptionsFactory.OPTION_KEY_OUTPUT_PATH);
            String[] excludes = commandLine.getOptionValues(OptionsFactory.OPTION_KEY_EXCLUDE);

            String inputPath = getInputPath(commandLine);

            try {
                ConfigurableApplicationContext applicationContext = getApplicationContext();
                applicationContext.addBeanFactoryPostProcessor(new ConfigurationRegisteringBeanFactoryPostProcessor(new Configuration(inputPath,
                    outputPath, outputTypes, excludes)));
                applicationContext.refresh();
                applicationContext.getBean(MigrationAnalysisExecutor.class).execute();
            } catch (RuntimeException re) {
                this.logger.debug("Analysis failed", re);
                this.logger.error("A failure occurred. Please see earlier output for details.");
                exit(-1);
            }
        } catch (ParseException e) {
            displayUsage();
            exit(-1);
        }
    }

    private void displayUsage() {
        PrintWriter writer = new PrintWriter(System.out);

        HelpFormatter helpFormatter = new HelpFormatter();

        writer.println(String.format("Usage: migration-analysis.%s <inputPath> [OPTION]...", getScriptSuffix()));
        printHeader("Description:", writer);
        helpFormatter.printWrapped(writer, OPTIONS_WIDTH, OPTIONS_INDENT, DESCRIPTION);
        printHeader("Options:", writer);
        helpFormatter.printOptions(writer, OPTIONS_WIDTH, OPTIONS, OPTIONS_INDENT, OPTIONS_INDENT);

        writer.flush();
    }

    private String getScriptSuffix() {
        return isWindows() ? "bat" : "sh";
    }

    private boolean isWindows() {
        return File.separatorChar == '\\';
    }

    private void printHeader(String header, PrintWriter writer) {
        writer.println();
        writer.println(header);
        writer.println();
    }

    private String getInputPath(CommandLine commandLine) throws ParseException {
        String[] args = commandLine.getArgs();
        if (args.length == 1) {
            return args[0];
        } else {
            throw new ParseException("Only one argument expected");
        }
    }

    protected abstract ConfigurableApplicationContext getApplicationContext();

    protected abstract void exit(int code);

    private static final class ConfigurationRegisteringBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

        private final Configuration configuration;

        private ConfigurationRegisteringBeanFactoryPostProcessor(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
            beanFactory.registerSingleton("configuration", this.configuration);
        }
    }
}
