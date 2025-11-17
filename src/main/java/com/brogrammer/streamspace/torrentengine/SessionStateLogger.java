package com.brogrammer.streamspace.torrentengine;

import bt.metainfo.Torrent;
import bt.torrent.TorrentSessionState;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Optional;

@Slf4j
public class SessionStateLogger {

    private static final String TORRENT_INFO = "Downloading %s (%,d B)";
    private static final String DURATION_INFO ="Elapsed time: %s\t\tRemaining time: %s";
    private static final String RATE_FORMAT = "%4.1f %s/s";
    private static final String SESSION_INFO = "Peers: %2d\t\tDown: " + RATE_FORMAT + "\t\tUp: " + RATE_FORMAT + "\t\t";

    private static final String WHITESPACES = "\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020\u0020";

    private static final String LOG_ENTRY = "Downloading.. Peers: %s; Down: " + RATE_FORMAT +
            "; Up: " + RATE_FORMAT + "; %.2f%% complete; Remaining time: %s";

    private static final String LOG_ENTRY_SEED = "Seeding.. Peers: %s; Up: " + RATE_FORMAT;

    private Optional<Torrent> torrent;
    private volatile long started;
    private volatile long downloaded;
    private volatile long uploaded;
    private final DownloadProgressHandler downloadProgressHandler;
    private final TorrentDownloadManager torrentDownloadManager;

    public SessionStateLogger(DownloadProgressHandler downloadProgressHandler, TorrentDownloadManager torrentDownloadManager) {
        started = System.currentTimeMillis();
        this.torrent = Optional.empty();
        this.downloadProgressHandler = downloadProgressHandler;
        this.torrentDownloadManager = torrentDownloadManager;
        printTorrentInfo();
    }

    public void setTorrent(Torrent torrent) {
        this.torrent = Optional.of(torrent);
    }

    public synchronized void printTorrentState(TorrentSessionState sessionState) {

        try {
            long downloaded = sessionState.getDownloaded();
            long uploaded = sessionState.getUploaded();

            String elapsedTime = getElapsedTime();
            String remainingTime = getRemainingTime(downloaded - this.downloaded,
                    sessionState.getPiecesRemaining(), sessionState.getPiecesNotSkipped());
            log.debug(String.format(DURATION_INFO, elapsedTime, remainingTime));


            Rate downRate = new Rate(downloaded - this.downloaded);
            Rate upRate = new Rate(uploaded - this.uploaded);
            int peerCount = sessionState.getConnectedPeers().size();
            String sessionInfo = String.format(SESSION_INFO, peerCount, downRate.getQuantity(), downRate.getMeasureUnit(),
                    upRate.getQuantity(), upRate.getMeasureUnit());
            log.debug(sessionInfo);

            int completed = sessionState.getPiecesComplete();
            double completePercents = getCompletePercentage(sessionState.getPiecesTotal(), completed);
            // double requiredPercents = getTargetPercentage(sessionState.getPiecesTotal(), completed, sessionState.getPiecesRemaining());
            // graphics.putString(0, 4, getProgressBar(completePercents, requiredPercents));

            if (torrent.isPresent()) {
                downloadProgressHandler.sendProgressUpdate(torrent.get().getTorrentId().toString().toUpperCase(),
                        String.format("%.2f%%", completePercents),
                        String.format(RATE_FORMAT, downRate.getQuantity(), downRate.getMeasureUnit()),
                        String.format(RATE_FORMAT, upRate.getQuantity(), upRate.getMeasureUnit()),
                        peerCount,
                        String.format("%s", remainingTime), Boolean.FALSE);
                //log.info(String.format("%s", elapsedTime));
            }

            boolean complete = (sessionState.getPiecesRemaining() == 0);
            if (complete) {
                torrentDownloadManager.onComplete(torrent.get().getTorrentId().toString().toUpperCase());
                downloadProgressHandler.sendProgressUpdate(torrent.get().getTorrentId().toString().toUpperCase(),
                        String.format("%.2f%%", completePercents),
                        String.format(RATE_FORMAT, downRate.getQuantity(), downRate.getMeasureUnit()),
                        String.format(RATE_FORMAT, upRate.getQuantity(), upRate.getMeasureUnit()),
                        peerCount,
                        String.format("%s", remainingTime), Boolean.TRUE);
                //log.info("Download is complete. Press Ctrl-C to stop seeding and exit.");
                log.info("Download is complete.");
            }

            if (complete) {
                log.debug(String.format(LOG_ENTRY_SEED, peerCount, upRate.getQuantity(), upRate.getMeasureUnit()));
            } else {
                log.debug(String.format(LOG_ENTRY, peerCount, downRate.getQuantity(), downRate.getMeasureUnit(),
                        upRate.getQuantity(), upRate.getMeasureUnit(), completePercents, remainingTime));
            }

            this.downloaded = downloaded;
            this.uploaded = uploaded;

        } catch (Throwable e) {
            log.error("Unexpected error when printing session state", e);
        }
    }

    private void printTorrentInfo() {
        printTorrentNameAndSize(torrent);
    }

    private void printTorrentNameAndSize(Optional<Torrent> torrent) {
        String name = torrent.isPresent() ? torrent.get().getName() : "";
        long size = torrent.map(Torrent::getSize).orElse(0L);
        log.info(String.format(TORRENT_INFO, name, size));
    }

    private String getElapsedTime() {
        Duration elapsed = Duration.ofMillis(System.currentTimeMillis() - started);
        return formatDuration(elapsed);
    }

    private String getRemainingTime(long downloaded, int piecesRemaining, int piecesTotal) {
        String remainingStr;
        if (piecesRemaining == 0) {
            remainingStr = "-" + WHITESPACES;
        } else if (downloaded == 0 || !torrent.isPresent()) {
            remainingStr = "\u221E" + WHITESPACES; // infinity
        } else {
            long size = torrent.get().getSize();
            double remaining = piecesRemaining / ((double) piecesTotal);
            long remainingBytes = (long) (size * remaining);
            Duration remainingTime = Duration.ofSeconds(remainingBytes / downloaded);
            // overwrite trailing chars with whitespaces if there are any
            remainingStr = formatDuration(remainingTime) + WHITESPACES;
        }
        return remainingStr;
    }

    private static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format("%d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }

    /*private String getProgressBar(double completePercents, double requiredPercents) throws IOException {
        int completeInt = (int) completePercents;
        int requiredInt = (int) requiredPercents;

        int width = graphics.getSize().getColumns() - 25;
        if (width < 0) {
            return "Progress: " + completeInt + "% (req.: " + requiredInt + "%)";
        }

        String s = "Progress: [%-" + width + "s] %d%%";
        char[] bar = new char[width];
        double shrinkFactor = width / 100d;
        int bound = (int) (completeInt * shrinkFactor);
        Arrays.fill(bar, 0, bound, '#');
        Arrays.fill(bar, bound, bar.length, ' ');
        if (completeInt != requiredInt) {
            bar[(int) (requiredInt * shrinkFactor) - 1] = '|';
        }
        return String.format(s, String.valueOf(bar), completeInt);
    }*/

    private double getCompletePercentage(int total, int completed) {
        return completed / ((double) total) * 100;
    }

    private double getTargetPercentage(int total, int completed, int remaining) {
        return (completed + remaining) / ((double) total) * 100;
    }

    private static class Rate {

        private long bytes;
        private double quantity;
        private String measureUnit;

        Rate(long delta) {
            if (delta < 0) {
//                throw new IllegalArgumentException("delta: " + delta);
                // TODO: this is a workaround for some nasty bug in the session state,
                // due to which the delta is sometimes (very seldom) negative
                // To not crash the UI let's just skip the problematic 'tick' and pretend that nothing was received
                // instead of throwing an exception
                log.warn("Negative delta: " + delta + "; will not re-calculate rate");
                delta = 0;
                quantity = 0;
                measureUnit = "B";
            } else if (delta < (2 << 9)) {
                quantity = delta;
                measureUnit = "B";
            } else if (delta < (2 << 19)) {
                quantity = delta / (2 << 9);
                measureUnit = "KB";
            } else {
                quantity = ((double) delta) / (2 << 19);
                measureUnit = "MB";
            }
            bytes = delta;
        }

        public long getBytes() {
            return bytes;
        }

        public double getQuantity() {
            return quantity;
        }

        public String getMeasureUnit() {
            return measureUnit;
        }
    }

}
