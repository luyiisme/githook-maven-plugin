package org.sandbox;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Mojo(name = "install", defaultPhase = LifecyclePhase.INITIALIZE)
public final class GitHookInstallMojo extends AbstractMojo {

    private static final String SHEBANG = "#!/bin/sh";
    private static final Path HOOK_DIR_PATH = Paths.get(".git/hooks");

    @Parameter
    private Map<String, String> hooks;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (!Files.exists(HOOK_DIR_PATH)) {
            throw new MojoExecutionException("not a git repository");
        }
        for (Map.Entry<String, String> hook : hooks.entrySet()) {
            String hookName = hook.getKey();
            String finalScript = SHEBANG + '\n' + hook.getValue();
            try {
                Path script = Files.write(HOOK_DIR_PATH.resolve(hookName), finalScript.getBytes(), CREATE, TRUNCATE_EXISTING);
                script.toFile().setExecutable(true);
            } catch (IOException e) {
                throw new MojoExecutionException("could not write hook with name: " + hookName, e);
            }
        }
    }

}
