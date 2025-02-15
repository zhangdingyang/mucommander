/**
 * This file is part of muCommander, http://www.mucommander.com
 *
 * muCommander is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * muCommander is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mucommander.commons.file.archive.sevenzip;

import com.mucommander.commons.file.AbstractFile;
import com.mucommander.commons.file.archive.AbstractArchiveFile;
import com.mucommander.commons.file.archive.ArchiveFormatProvider;
import com.mucommander.commons.file.filter.ExtensionFilenameFilter;
import com.mucommander.commons.file.filter.FilenameFilter;
import com.mucommander.sevenzipjbindings.SevenZipJBindingROArchiveFile;

import net.sf.sevenzipjbinding.ArchiveFormat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class is the provider for the '7z' archive format implemented by {@link RarArchiveFile}.
 *
 * @author Arik Hadas
 */
public class SevenZipFormatProvider implements ArchiveFormatProvider {

    /** extensions of archive filenames */
    public static final String[] EXTENSIONS = new String[] {".7z", ".cb7"};

    /** signature of 7z files */
    private static final byte[] SIGNATURE = { 0x37, 0x7A, (byte) 0xBC, (byte) 0xAF, 0x27, 0x1C };

    @Override
    public AbstractArchiveFile getFile(AbstractFile file) throws IOException {
        return new SevenZipJBindingROArchiveFile(file, ArchiveFormat.SEVEN_ZIP, SIGNATURE);
    }

    @Override
    public FilenameFilter getFilenameFilter() {
        return new ExtensionFilenameFilter(EXTENSIONS);
    }

    @Override
	public List<String> getExtensions() {
		return Arrays.asList(EXTENSIONS);
	}
}
