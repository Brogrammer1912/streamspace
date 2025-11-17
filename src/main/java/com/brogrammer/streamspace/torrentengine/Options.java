/*
 * Copyright (c) 2016—2024 Akshath Sai Pittala, Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brogrammer.streamspace.torrentengine;

import lombok.Data;

import java.io.File;

@Data
public class Options {

    private File metainfoFile;
    private String magnetUri;
    private String torrentHash;
    private File targetDirectory;
    private boolean seedAfterDownloaded;
    private boolean sequential;
    private boolean enforceEncryption;
    private boolean disableUi;
    private boolean disableTorrentStateLogs;
    private boolean verboseLogging;
    private boolean traceLogging;
    private String iface;
    private Integer port;
    private Integer dhtPort;
    private boolean downloadAllFiles;

}
