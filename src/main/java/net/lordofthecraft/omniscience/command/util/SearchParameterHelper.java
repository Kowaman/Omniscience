package net.lordofthecraft.omniscience.command.util;

import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class SearchParameterHelper {

    public static List<String> suggestParameterCompletion(String partial) {
        List<String> results = Lists.newArrayList();
        if (partial != null && !partial.isEmpty() && partial.contains(":")) {
            String[] splitPartial = partial.split(":");
            Optional<ParameterHandler> oHandler = Omniscience.getParameterHandler(splitPartial[0]);
            oHandler.ifPresent(handler -> handler.suggestTabCompletion(splitPartial.length > 1 ? splitPartial[1] : null)
                    .ifPresent(completionResults -> {
                        completionResults.forEach(res -> results.add(splitPartial[0] + ":" + res));
                    }));
        } else {
            Stream<String> params = Omniscience.getParameters()
                    .stream()
                    .flatMap(parameterHandler -> parameterHandler.getAliases().stream());
            if (partial != null && !partial.isEmpty()) {
                params = params.filter(param -> param.toLowerCase().startsWith(partial.toLowerCase()));
            }
            params.forEach(param -> results.add(param + ":"));
        }

        return results;
    }
}
