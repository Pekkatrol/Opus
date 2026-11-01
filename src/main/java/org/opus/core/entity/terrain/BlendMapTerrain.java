package org.opus.core.entity.terrain;

public class BlendMapTerrain {

        TerrainTexture background;
        TerrainTexture redTexture;
        TerrainTexture greenTexture;
        TerrainTexture blueTexture;

        public BlendMapTerrain(TerrainTexture background, TerrainTexture redTexture, TerrainTexture greenTexture, TerrainTexture blueTexture) {
                this.background = background;
                this.redTexture = redTexture;
                this.greenTexture = greenTexture;
                this.blueTexture = blueTexture;
        }

        public TerrainTexture getBackground() {
                return background;
        }

        public TerrainTexture getRedTexture() {
                return redTexture;
        }

        public TerrainTexture getGreenTexture() {
                return greenTexture;
        }

        public TerrainTexture getBlueTexture() {
                return blueTexture;
        }
}
