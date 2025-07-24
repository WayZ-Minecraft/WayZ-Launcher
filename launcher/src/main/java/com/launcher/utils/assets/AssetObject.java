package com.launcher.utils.assets;

public class AssetObject {

	private String hash;

	private long size;

	public String getHash() { return this.hash; }

	public long getSize() { return this.size; }

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if ((other == null) || (getClass() != other.getClass()))
			return false;

		AssetObject that = (AssetObject) other;

		if (this.size != that.size)
			return false;
		return this.hash.equals(that.hash);
	}

	@Override
	public int hashCode() {
		int result = this.hash.hashCode();
		result = 31 * result + (int) (this.size ^ this.size >>> 32);
		return result;
	}
}
