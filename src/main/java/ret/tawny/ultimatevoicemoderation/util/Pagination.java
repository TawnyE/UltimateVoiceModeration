package ret.tawny.ultimatevoicemoderation.util;

import org.bukkit.command.CommandSender;
import java.util.List;

public class Pagination {

    public static void sendPage(CommandSender sender, List<String> lines, int page, int linesPerPage, String header) {
        int totalPages = (int) Math.ceil((double) lines.size() / linesPerPage);
        if (page < 1 || page > totalPages) {
            sender.sendMessage("§cInvalid page number.");
            return;
        }

        sender.sendMessage(header + " (Page " + page + "/" + totalPages + ")");
        int startIndex = (page - 1) * linesPerPage;
        int endIndex = Math.min(startIndex + linesPerPage, lines.size());

        for (int i = startIndex; i < endIndex; i++) {
            sender.sendMessage(lines.get(i));
        }
    }
}
