package com.almazov.diacompanion.model.gbm;

import com.almazov.diacompanion.model.tree.RegTree;
import com.almazov.diacompanion.model.util.FVec;
import com.almazov.diacompanion.model.util.ModelReader;

import java.io.IOException;
import java.io.Serializable;

/**
 * Gradient boosted tree implementation.
 */
public class GBTree extends GBBase {
    ModelParam mparam;
    RegTree[][] _groupTrees;
    private RegTree[] trees;

    GBTree() {
        // do nothing
    }

    @Override
    public void loadModel(ModelReader reader, boolean with_pbuffer) throws IOException {
        mparam = new ModelParam(reader);

        trees = new RegTree[mparam.num_trees];
        for (int i = 0; i < mparam.num_trees; i++) {
            trees[i] = new RegTree();
            trees[i].loadModel(reader);
        }

        int[] tree_info = mparam.num_trees > 0 ? reader.readIntArray(mparam.num_trees) : new int[0];

        if (mparam.num_pbuffer != 0 && with_pbuffer) {
            reader.skip(4 * mparam.predBufferSize());
            reader.skip(4 * mparam.predBufferSize());
        }

        _groupTrees = new RegTree[mparam.num_output_group][];
        for (int i = 0; i < mparam.num_output_group; i++) {
            int treeCount = 0;
            for (int k : tree_info) {
                if (k == i) {
                    treeCount++;
                }
            }

            _groupTrees[i] = new RegTree[treeCount];
            treeCount = 0;

            for (int j = 0; j < tree_info.length; j++) {
                if (tree_info[j] == i) {
                    _groupTrees[i][treeCount++] = trees[j];
                }
            }
        }
    }

    @Override
    public double[] predict(FVec feat, int ntree_limit) {
        double[] preds = new double[mparam.num_output_group];
        for (int gid = 0; gid < mparam.num_output_group; gid++) {
            preds[gid] = pred(feat, gid, ntree_limit);
        }
        return preds;
    }

    @Override
    public double predictSingle(FVec feat, int ntree_limit) {
        if (mparam.num_output_group != 1) {
            throw new IllegalStateException(
                    "Can't invoke predictSingle() because this model outputs multiple values: "
                            + mparam.num_output_group);
        }
        return pred(feat, 0, ntree_limit);
    }

    double pred(FVec feat, int bst_group, int ntree_limit) {
        RegTree[] trees = _groupTrees[bst_group];
        int treeleft = ntree_limit == 0 ? trees.length : Math.min(ntree_limit, trees.length);

        double psum = 0;
        for (int i = 0; i < treeleft; i++) {
            psum += trees[i].getLeafValue(feat, 0);
        }

        return psum;
    }

    @Override
    public int[] predictLeaf(FVec feat, int ntree_limit) {
        return predPath(feat, ntree_limit);
    }


    int[] predPath(FVec feat, int ntree_limit) {
        int treeleft = ntree_limit == 0 ? trees.length : Math.min(ntree_limit, trees.length);

        int[] leafIndex = new int[treeleft];
        for (int i = 0; i < treeleft; i++) {
            leafIndex[i] = trees[i].getLeafIndex(feat, 0);
        }
        return leafIndex;
    }


    static class ModelParam implements Serializable {
        /*! \brief number of trees */
        final int num_trees;
        /*! \brief number of root: default 0, means single tree */
        final int num_roots;
        /*! \brief number of features to be used by trees */
        final int num_feature;
        /*! \brief size of prediction buffer allocated used for buffering */
        final long num_pbuffer;
        /*!
         * \brief how many output group a single instance can produce
         *  this affects the behavior of number of output we have:
         *    suppose we have n instance and k group, output will be k*n
         */
        final int num_output_group;
        /*! \brief size of leaf vector needed in tree */
        final int size_leaf_vector;
        /*! \brief reserved parameters */
        final int[] reserved;

        ModelParam(ModelReader reader) throws IOException {
            num_trees = reader.readInt();
            num_roots = reader.readInt();
            num_feature = reader.readInt();
            reader.readInt(); // read padding
            num_pbuffer = reader.readLong();
            num_output_group = reader.readInt();
            size_leaf_vector = reader.readInt();
            reserved = reader.readIntArray(31);
            reader.readInt(); // read padding
        }

        long predBufferSize() {
            return num_output_group * num_pbuffer * (size_leaf_vector + 1);
        }
    }

}
