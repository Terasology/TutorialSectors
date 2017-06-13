/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.sectorTesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.context.Context;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.players.event.OnPlayerSpawnedEvent;
import org.terasology.registry.In;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TestSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(TestSystem.class);

    @In
    private EntityManager entityManager;

    @In
    private Context context;

    @Override
    public void update(float delta) {

        //Global entities
        //Currently, getEntitiesWith only works for entities in the global cache
        //for (EntityRef entity : entityManager.getEntitiesWith(TestComponent.class)) {
        for (EntityRef entity : entityManager.getAllEntities()) {
            if(entity.hasComponent(TestComponent.class)) {
                entity.getComponent(TestComponent.class).testValue++;
                logger.info("" + entity.getComponent(TestComponent.class).testValue);
            }
        }

    }

    @ReceiveEvent
    public void onPlayerSpawn(OnPlayerSpawnedEvent event, EntityRef player) {

        //Global entity
        EntityRef entity = entityManager.getGlobalCache().create();
        TestComponent testComp = new TestComponent();
        testComp.testValue = 0;
        entity.saveComponent(testComp);

        //Sector entity
        //EntityRef entity = entityManager.createSectorEntity();
        EntityRef entity2 = entityManager.getSectorCache().create();
        TestComponent testComp2 = new TestComponent();
        testComp2.testValue = 20000;
        entity2.saveComponent(testComp2);
    }
}
