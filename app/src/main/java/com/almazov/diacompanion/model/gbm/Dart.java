package com.almazov.diacompanion.model.gbm;

import com.almazov.diacompanion.model.tree.RegTree;
import com.almazov.diacompanion.model.util.FVec;
import com.almazov.diacompanion.model.util.ModelReader;

import java.io.IOException;

/**
 * Gradient boosted DART tree implementation.
 */
public class Dart extends GBTree {
    private float[] weightDrop;

    Dart() {
        // do nothing
    }

    @Override
    public void loadModel(ModelReader reader, boolean with_pbuffer) throws IOException {
        super.loadModel(reader, with_pbuffer);
        if (mparam.num_trees != 0) {
            long size = reader.readLong();
            weightDrop = reader.readFloatArray((int) size);
        }
    }

    double pred(FVec feat, int bst_group, int ntree_limit) {
        RegTree[] trees = _groupTrees[bst_group];
        int treeleft = ntree_limit == 0 ? trees.length : Math.min(ntree_limit, trees.length);

        double psum = 0;
        for (int i = 0; i < treeleft; i++) {
            psum += weightDrop[i] * trees[i].getLeafValue(feat, 0);
        }

        return psum;
    }
}
