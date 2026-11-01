package org.opus.core.entity;

import org.joml.Vector3f;
import org.opus.core.SpotLight;
import org.opus.core.entity.terrain.Terrain;
import org.opus.core.lighting.DirectionalLight;
import org.opus.core.lighting.PointLight;

import java.util.ArrayList;
import java.util.List;

import static org.opus.core.utils.Consts.AMBIENT_LIGHT;

public class SceneManager {

        private List<Entity> entities;
        private List<Terrain> terrains;

        private SpotLight[] spotLights;
        private PointLight[] pointLights;
        private DirectionalLight directionalLight;
        private Vector3f ambientLight;
        private float lightAngle;
        private float spotAngle;
        private float spotInc = 1;

        public SceneManager(float lightAngle) {
                entities = new ArrayList<>();
                terrains = new ArrayList<>();

                ambientLight = AMBIENT_LIGHT;
                this.lightAngle = lightAngle;
        }

        public float getLightAngle() {
                return lightAngle;
        }

        public void setLightAngle(float lightAngle) {
                this.lightAngle = lightAngle;
        }

        public float getSpotAngle() {
                return spotAngle;
        }

        public void setSpotAngle(float spotAngle) {
                this.spotAngle = spotAngle;
        }

        public float getSpotInc() {
                return spotInc;
        }

        public void setSpotInc(float spotInc) {
                this.spotInc = spotInc;
        }

        public Vector3f getAmbientLight() {
                return ambientLight;
        }

        public void setAmbientLight(Vector3f ambientLight) {
                this.ambientLight = ambientLight;
        }

        public void setAmbientLight(float x, float y, float z) {
                ambientLight = new Vector3f(x, y, z);
        }

        public DirectionalLight getDirectionalLight() {
                return directionalLight;
        }

        public void setDirectionalLight(DirectionalLight directionalLight) {
                this.directionalLight = directionalLight;
        }

        public PointLight[] getPointLights() {
                return pointLights;
        }

        public void setPointLights(PointLight[] pointLights) {
                this.pointLights = pointLights;
        }

        public SpotLight[] getSpotLights() {
                return spotLights;
        }

        public void setSpotLights(SpotLight[] spotLights) {
                this.spotLights = spotLights;
        }

        public List<Terrain> getTerrains() {
                return terrains;
        }

        public void setTerrains(List<Terrain> terrains) {
                this.terrains = terrains;
        }

        public void addTerrain(Terrain terrain) {
                this.terrains.add(terrain);
        }

        public List<Entity> getEntities() {
                return entities;
        }

        public void setEntities(List<Entity> entities) {
                this.entities = entities;
        }

        public void addEntity(Entity entity) {
                this.entities.add(entity);
        }

        public void incLightAngle(float increment) {
                this.lightAngle += increment;
        }

        public void incSpotAngle(float increment) {
                this.spotAngle *= increment;
        }
}
