package usr.skyswimmer.gitplugin;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import com.google.gson.JsonPrimitive;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.plugins.IPolyscriptPlugin;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public class GitPlugin implements IPolyscriptPlugin {

    @Override
    public String name() {
        return "git";
    }

    @Override
    public void init(PolyScriptEngine engine) {
    }

    @Override
    public void populateContexts(PolyScriptEngine engine, PolyScript script, JsonVariablesProcessor processor,
            JsonVariablesContext local, JsonVariablesContext global) {
        // Check git
        File gitRepo = new File(script.getWorkingDirectory(), ".git");
        if (gitRepo.exists() && gitRepo.isDirectory()) {
            // Git repository available

            // Open repository
            Git client;
            try {
                client = Git.open(new File(script.getWorkingDirectory(), "deps/connective-http"));
            } catch (Exception e) {
                engine.getLogger().error("Git plugin failed to load: failed to load repository", e);
                return;
            }

            // Get repo
            Repository repo = client.getRepository();

            try {
                // Get branch details
                String branch = repo.getBranch();
                String fullBranch = repo.getFullBranch();
                String ref = repo.findRef("HEAD").getTarget().getName();
                if (!ref.equals("HEAD")) {
                    // On branch
                    local.assignVariable("git.branch", new JsonPrimitive(branch));
                } else {
                    // Detached
                    String tag = client.describe().setAbbrev(0).setTags(true).call();
                    if (tag != null) {
                        // Check tag
                        Ref refTag = repo.findRef("refs/tags/" + tag);
                        if (refTag != null) {
                            String commit = refTag.getObjectId().getName();
                            if (!commit.equals(fullBranch))
                                tag = null;
                        }
                    }

                    // Check result
                    if (tag != null) {
                        // On tag
                        local.assignVariable("git.branch", new JsonPrimitive(tag));
                    } else {
                        // On commit
                        String currentCommit = fullBranch;
                        local.assignVariable("git.branch", new JsonPrimitive(currentCommit));
                    }
                }
            } catch (Exception e) {
                engine.getLogger().error("Git plugin encountered an error: failed to assign branch information", e);
            }

            try {
                // Current commit details
                ObjectId id = repo.resolve(repo.getFullBranch());
                RevWalk revWalk = new RevWalk(repo);
                RevCommit currentCommit = revWalk.parseCommit(id);
                revWalk.close();

                // Set commit
                assignCommitInfo(currentCommit, "git.currentcommit", local);
            } catch (Exception e) {
                engine.getLogger().error("Git plugin encountered an error: failed to assign current commit information",
                        e);
            }
        }
    }

    private void assignCommitInfo(RevCommit currentCommit, String key, JsonVariablesContext ctx) {
        String prefix = key;
        if (!prefix.isEmpty())
            prefix += ".";
        ctx.assignVariable(prefix + "id", new JsonPrimitive(currentCommit.getName()));
        ctx.assignVariable(prefix + "message", new JsonPrimitive(cleanFullMessage(currentCommit.getFullMessage())));
        ctx.assignVariable(prefix + "message.full",
                new JsonPrimitive(cleanFullMessage(currentCommit.getFullMessage())));
        ctx.assignVariable(prefix + "message.short", new JsonPrimitive(currentCommit.getShortMessage()));
        ctx.assignVariable(prefix + "message.firstline", new JsonPrimitive(currentCommit.getFirstMessageLine()));
    }

    private String cleanFullMessage(String fullMessage) {
        fullMessage = fullMessage.replace("\r", "");
        while (fullMessage.startsWith("\n"))
            fullMessage = fullMessage.substring(1);
        while (fullMessage.endsWith("\n"))
            fullMessage = fullMessage.substring(0, fullMessage.length() - 1);
        return fullMessage.trim();
    }

}
