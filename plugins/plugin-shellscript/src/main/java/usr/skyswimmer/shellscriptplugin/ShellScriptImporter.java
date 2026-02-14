package usr.skyswimmer.shellscriptplugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.UUID;

import org.asf.connective.io.PrependedBufferStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import usr.skyswimmer.polyscriptrunner.PolyScript;
import usr.skyswimmer.polyscriptrunner.PolyScriptEngine;
import usr.skyswimmer.polyscriptrunner.importers.IPolyscriptImporter;
import usr.skyswimmer.quicktoolsutils.json.JsonUtils;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesContext;
import usr.skyswimmer.quicktoolsutils.json.JsonVariablesProcessor;

public class ShellScriptImporter implements IPolyscriptImporter {

    @Override
    public String name() {
        return "shellscript";
    }

    @Override
    public boolean supportDirectories() {
        return false;
    }

    @Override
    public boolean importFile(String importRelative, String targetVariableName, File file, PolyScriptEngine engine,
            PolyScript script, JsonVariablesProcessor processor, JsonVariablesContext target) throws IOException {
        // Create random key
        String random = UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString()
                + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString()
                + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString()
                + UUID.randomUUID().toString();

        // Generate script
        StringBuilder shell = new StringBuilder();
        shell.append("#!/bin/bash\n");

        // Write helper source calls
        if (script.getScriptJson().has("shellenv")) {
            JsonObject shellEnvDefs = JsonUtils.getObjectOrError("polyscript", script.getScriptJson(), "shellenv");
            if (shellEnvDefs.has(importRelative)) {
                // Load defs
                JsonElement ele = shellEnvDefs.get(importRelative);
                if (ele.isJsonObject())
                    throw new IOException("Invalid shell environment variable: " + importRelative
                            + ": expected json array or string entry");
                if (!ele.isJsonArray()) {
                    JsonArray arrT = new JsonArray();
                    arrT.add(ele);
                    ele = arrT;
                }
                for (JsonElement element : ele.getAsJsonArray()) {
                    // Check
                    String scriptEnv = JsonUtils.getStringOrError(importRelative, element);
                    File envFile = new File(scriptEnv);
                    if (!envFile.isAbsolute())
                        envFile = new File(script.getWorkingDirectory(), envFile.getPath());
                    if (!envFile.exists())
                        throw new IOException("Script environment helper file " + scriptEnv + " does not exist");
                    shell.append("function setup() {\n   :\n}\n");
                    shell.append("function finish() {\n   :\n}\n");
                    shell.append("function emitvars() {\n   :\n}\n");
                    shell.append("source '" + scriptEnv.replace("'", "'\"'\"'") + "' || exit 1\n");
                    shell.append("setup || exit 1\n");
                }
            }
        }

        // Write source call
        shell.append("cd \"$(dirname \"$1\")\" || exit 1\n");
        shell.append("source \"$1\" || exit 1\n");

        // Write emitter
        shell.append("function pushVariable() {\n");
        shell.append("    echo \"" + random + "\" KEYSTART\n");
        shell.append("    echo \"$1\"\n");
        shell.append("    echo \"" + random + "\" KEYEND\n");
        shell.append("    echo \"" + random + "\" VALSTART\n");
        shell.append("    echo \"$2\"\n");
        shell.append("    echo \"" + random + "\" VALEND\n");
        shell.append("}\n");
        shell.append("function pushArrayElement() {\n");
        shell.append("    echo \"" + random + "\" KEYSTARTARRAY\n");
        shell.append("    echo \"$1\"\n");
        shell.append("    echo \"" + random + "\" KEYENDARRAY\n");
        shell.append("    echo \"" + random + "\" VALSTARTARRAY\n");
        shell.append("    echo \"$2\"\n");
        shell.append("    echo \"" + random + "\" VALENDARRAY\n");
        shell.append("}\n");

        // Write push calls
        if (script.getScriptJson().has("shellenv")) {
            JsonObject shellEnvDefs = JsonUtils.getObjectOrError("polyscript", script.getScriptJson(), "shellenv");
            if (shellEnvDefs.has(importRelative)) {
                // Load defs
                JsonElement ele = shellEnvDefs.get(importRelative);
                if (ele.isJsonObject())
                    throw new IOException("Invalid shell environment variable: " + importRelative
                            + ": expected json array or string entry");
                if (!ele.isJsonArray()) {
                    JsonArray arrT = new JsonArray();
                    arrT.add(ele);
                    ele = arrT;
                }
                for (JsonElement element : ele.getAsJsonArray()) {
                    // Check
                    String scriptEnv = JsonUtils.getStringOrError(importRelative, element);
                    File envFile = new File(scriptEnv);
                    if (!envFile.isAbsolute())
                        envFile = new File(script.getWorkingDirectory(), envFile.getPath());
                    if (!envFile.exists())
                        throw new IOException("Script environment helper file " + scriptEnv + " does not exist");
                    shell.append("function setup() {\n   :\n}\n");
                    shell.append("function finish() {\n   :\n}\n");
                    shell.append("function emitvars() {\n   :\n}\n");
                    shell.append("source '" + scriptEnv.replace("'", "'\"'\"'") + "' || exit 1\n");
                    shell.append("emitvars || exit 1\n");
                }
            }
        }

        // Write finish source calls
        if (script.getScriptJson().has("shellenv")) {
            JsonObject shellEnvDefs = JsonUtils.getObjectOrError("polyscript", script.getScriptJson(), "shellenv");
            if (shellEnvDefs.has(importRelative)) {
                // Load defs
                JsonElement ele = shellEnvDefs.get(importRelative);
                if (ele.isJsonObject())
                    throw new IOException("Invalid shell environment variable: " + importRelative
                            + ": expected json array or string entry");
                if (!ele.isJsonArray()) {
                    JsonArray arrT = new JsonArray();
                    arrT.add(ele);
                    ele = arrT;
                }
                for (JsonElement element : ele.getAsJsonArray()) {
                    // Check
                    String scriptEnv = JsonUtils.getStringOrError(importRelative, element);
                    File envFile = new File(scriptEnv);
                    if (!envFile.isAbsolute())
                        envFile = new File(script.getWorkingDirectory(), envFile.getPath());
                    if (!envFile.exists())
                        throw new IOException("Script environment helper file " + scriptEnv + " does not exist");
                    shell.append("function setup() {\n   :\n}\n");
                    shell.append("function finish() {\n   :\n}\n");
                    shell.append("function emitvars() {\n   :\n}\n");
                    shell.append("source '" + scriptEnv.replace("'", "'\"'\"'") + "' || exit 1\n");
                    shell.append("finish || exit 1\n");
                }
            }
        }

        // Create file
        File tempFile = File.createTempFile(
                "bashtempscript-" + UUID.randomUUID().toString() + "-temp-polyscript-importer-",
                "-" + UUID.randomUUID().toString() + ".sh");
        try {
            // Write script
            Files.writeString(tempFile.toPath(), shell.toString());

            // Create bash process
            ProcessBuilder builder = new ProcessBuilder("bash", tempFile.getCanonicalPath(), file.getCanonicalPath());
            builder.directory(script.getWorkingDirectory());
            Process proc = builder.start();

            // Read all output lines
            String buffer = "";
            String key = "";
            boolean isArray = false;
            boolean valueStart = false;
            boolean keyStart = false;
            boolean active = false;
            PrependedBufferStream strm = new PrependedBufferStream(proc.getInputStream());
            while (true) {
                String line = readStreamLine(strm);
                if (line == null)
                    break;
                if (!active) {
                    // Check line
                    // Check key start magic
                    if (line.equals(random + " KEYSTART")) {
                        active = true;
                        isArray = false;
                        keyStart = true;
                    } else if (line.equals(random + " KEYSTARTARRAY")) {
                        active = true;
                        isArray = true;
                        keyStart = true;
                    }
                } else {
                    // Check state
                    if (keyStart) {
                        // In key
                        // Wait for key end
                        if (isArray) {
                            // Check for magic
                            if (line.equals(random + " KEYENDARRAY")) {
                                // End of key, save key
                                key = buffer;
                                keyStart = false;
                                buffer = "";
                            } else {
                                // Add line to buffer
                                if (!buffer.isEmpty())
                                    buffer += "\n";
                                buffer += line;
                            }
                        } else {
                            // Check for magic
                            if (line.equals(random + " KEYEND")) {
                                // End of key, save key
                                key = buffer;
                                keyStart = false;
                                buffer = "";
                            } else {
                                // Add line to buffer
                                if (!buffer.isEmpty())
                                    buffer += "\n";
                                buffer += line;
                            }
                        }
                    } else if (!valueStart) {
                        // We have the key, just not the value
                        if (isArray) {
                            // Check for magic
                            if (line.equals(random + " VALSTARTARRAY")) {
                                valueStart = true;
                            }
                        } else {
                            // Check for magic
                            if (line.equals(random + " VALSTART")) {
                                valueStart = true;
                            }
                        }
                    } else {
                        // In value
                        // Wait for end
                        if (isArray) {
                            // Check for magic
                            if (line.equals(random + " VALENDARRAY")) {
                                // Got value
                                String value = buffer;

                                // Update
                                if (!target.hasVariable(key) || !target.getVariable(key).isJsonArray())
                                    target.assignVariable(key, new JsonArray());
                                target.getVariable(key).getAsJsonArray().add(new JsonPrimitive(value));

                                // Reset
                                active = false;
                                keyStart = false;
                                valueStart = false;
                                buffer = "";
                                key = "";
                            } else {
                                // Add line to buffer
                                if (!buffer.isEmpty())
                                    buffer += "\n";
                                buffer += line;
                            }
                        } else {
                            // Check for magic
                            if (line.equals(random + " VALEND")) {
                                // Got value
                                String value = buffer;

                                // Update
                                target.assignVariable(key, new JsonPrimitive(value));

                                // Reset
                                active = false;
                                keyStart = false;
                                valueStart = false;
                                buffer = "";
                                key = "";
                            } else {
                                // Add line to buffer
                                if (!buffer.isEmpty())
                                    buffer += "\n";
                                buffer += line;
                            }
                        }
                    }
                }
            }

            // Discard error
            proc.getErrorStream().readAllBytes();

            // Close
            proc.destroyForcibly();
        } catch (Exception e) {
            throw new IOException("Failed to launch script shell for variable processing", e);
        } finally {
            tempFile.delete();
        }

        // Return
        return true;
    }

    protected String readStreamLine(PrependedBufferStream strm) throws IOException {
        // Read a number of bytes
        byte[] content = new byte[20480];
        int read = strm.read(content, 0, content.length);
        if (read <= -1) {
            // Failed
            return null;
        } else {
            // Trim array
            content = Arrays.copyOfRange(content, 0, read);

            // Find newline
            String newData = new String(content, "UTF-8");
            if (newData.contains("\n")) {
                // Found newline
                String line = newData.substring(0, newData.indexOf("\n"));
                int offset = line.length() + 1;
                int returnLength = content.length - offset;
                if (returnLength > 0) {
                    // Return
                    strm.returnToBuffer(Arrays.copyOfRange(content, offset, content.length));
                }
                return line.replace("\r", "");
            } else {
                // Read more
                while (true) {
                    byte[] addition = new byte[20480];
                    read = strm.read(addition, 0, addition.length);
                    if (read <= -1) {
                        // Failed
                        strm.returnToBuffer(content);
                        return null;
                    }

                    // Trim
                    addition = Arrays.copyOfRange(addition, 0, read);

                    // Append
                    byte[] newContent = new byte[content.length + addition.length];
                    for (int i = 0; i < content.length; i++)
                        newContent[i] = content[i];
                    for (int i = content.length; i < newContent.length; i++)
                        newContent[i] = addition[i - content.length];
                    content = newContent;

                    // Find newline
                    newData = new String(content, "UTF-8");
                    if (newData.contains("\n")) {
                        // Found newline
                        String line = newData.substring(0, newData.indexOf("\n"));
                        int offset = line.length() + 1;
                        int returnLength = content.length - offset;
                        if (returnLength > 0) {
                            // Return
                            strm.returnToBuffer(Arrays.copyOfRange(content, offset, content.length));
                        }
                        return line.replace("\r", "");
                    }
                }
            }
        }
    }

}
